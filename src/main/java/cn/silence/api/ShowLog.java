package cn.silence.api;

import cn.silence.utils.Assert;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author rainofsilence
 * @version 1.0.0
 * @since 2023/05/20 15:49:26
 */
public class ShowLog {

    /**
     * @param localRepoPath
     * @param author
     * @param since
     * @param until
     * @throws IOException
     * @throws GitAPIException
     */
    public static void printCommitEntryPaths(String localRepoPath, String author, String since, String until) throws IOException, GitAPIException {
        Assert.notBlank(localRepoPath,"localRepoPath cannot be null");
        Assert.notBlank(author,"author cannot be null");
        Assert.notBlank(since,"since cannot be null");
        Assert.notBlank(until,"until cannot be null");
        Set<String> entryList = getCommitEntryPaths(localRepoPath, author, since, until);
        entryList.forEach(System.out::println);
        System.out.printf("ShowLog.printCommitEntryPaths success and entryList.size = [%s]%n", entryList.size());
    }

    /**
     * git log --author=[] since..until
     *
     * @param localRepoPath
     * @param author
     * @param since
     * @param until
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    public static Set<String> getCommitEntryPaths(String localRepoPath, String author, String since, String until) throws IOException, GitAPIException {
        try (Git git = Git.open(new File(localRepoPath))) {
            Repository repo = git.getRepository();
            ObjectId sinceObjectId = repo.resolve(since);
            ObjectId untilObjectId = repo.resolve(until);
            Iterable<RevCommit> logs = git.log().addRange(sinceObjectId, untilObjectId).call();
            Iterator<RevCommit> iterator = logs.iterator();
            RevCommit old;
            RevCommit last = null;
            Set<String> updateCommitSet = new TreeSet<>(Comparator.reverseOrder());
            Map<String, List<CommitLog>> commitLogMap = new HashMap<>();
            boolean isLastNode = false; // 是否最后一个节点
            while (iterator.hasNext() || (isLastNode && last != null)) {
                if (last == null) {
                    last = iterator.next();
                    isLastNode = !iterator.hasNext();
                    continue;
                }
                if (!author.contains(last.getAuthorIdent().getName())) {
                    last = null;
                    isLastNode = !iterator.hasNext();
                    continue;
                }
                String commitDateStr = getCommitDateStr(last);
                Set<CommitLog> commitLogs;
                if (iterator.hasNext()) {
                    old = iterator.next();
                    commitLogs = DiffFilesInCommit.listDiff(repo, git, old.getId().getName(), last.getId().getName());
                    if (author.equals(old.getAuthorIdent().getName())) last = old;
                    else last = null;
                } else {
                    commitLogs = DiffFilesInCommit.listDiff(repo, git, sinceObjectId.getName(), last.getId().getName());
                    last = null;
                }
                for (CommitLog c : commitLogs) {
                    c.setCommitDateStr(commitDateStr);
                    List<CommitLog> cls = commitLogMap.getOrDefault(c.getPath(), new ArrayList<>());
                    cls.add(c);
                    commitLogMap.put(c.getPath(), cls);
                }
                isLastNode = !iterator.hasNext();
            }
            commitLogMap.keySet().forEach(key -> {
                List<CommitLog> commitLogs = commitLogMap.get(key);
                // 按提交时间DESC 如果changType为DELETE则忽略
                commitLogs = commitLogs.stream()
                        .sorted(Comparator.comparing(CommitLog::getCommitDateStr).reversed())
                        .collect(Collectors.toList());
                if (commitLogs.get(0).getChangType() != DiffEntry.ChangeType.DELETE) {
                    updateCommitSet.add(commitLogs.get(0).getPath());
                }
            });
            return updateCommitSet;
        }
    }

    /**
     * 获取提交时间
     *
     * @param commit
     * @return
     */
    private static String getCommitDateStr(RevCommit commit) {
        Instant commitInstant = Instant.ofEpochSecond(commit.getCommitTime());
        ZoneId zoneId = commit.getAuthorIdent().getTimeZone().toZoneId();
        ZonedDateTime authorDateTime = ZonedDateTime.ofInstant(commitInstant, zoneId);
        String gitDateTimeFormatString = "EEE MMM dd HH:mm:ss yyyy Z";
        return authorDateTime.format(DateTimeFormatter.ofPattern(gitDateTimeFormatString));
    }
}
