package cn.silence.api;

import cn.silence.utils.Assert;
import cn.silence.utils.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.AuthorRevFilter;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.silence.utils.StrLenConstant.DIVIDING_LINE;

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
    public static void printChangeFilePaths(String localRepoPath, String author, String since, String until) throws IOException, GitAPIException {
        Set<String> entryList = getChangeFilePaths(localRepoPath, author, since, until);
        System.out.println("\nprint final changFile list\n" + DIVIDING_LINE);
        entryList.forEach(System.out::println);
        System.out.printf(DIVIDING_LINE + "\nsize = [%s]%n", entryList.size());
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
    public static Set<String> getChangeFilePaths(String localRepoPath, String author, String since, String until) throws IOException, GitAPIException {
        Assert.notBlank(localRepoPath, "localRepoPath cannot be null");
        Assert.notBlank(author, "author cannot be null");
        Assert.notBlank(since, "since cannot be null");
        Assert.notBlank(until, "until cannot be null");
        try (Git git = Git.open(new File(localRepoPath))) {
            Repository repo = git.getRepository();
            ObjectId sinceObjectId = repo.resolve(since);
            ObjectId untilObjectId = repo.resolve(until);
            Iterable<RevCommit> logs = git.log().addRange(sinceObjectId, untilObjectId).setRevFilter(AuthorRevFilter.create(author)).call();
            Iterator<RevCommit> iterator = logs.iterator();
            Set<String> changFileSet = new TreeSet<>(Comparator.reverseOrder());
            Map<String, List<DiffFilesInCommit.ChangeFile>> changFileHashMap = new HashMap<>();
            boolean isLastNode = false; // 是否最后一个节点
            while (iterator.hasNext() || isLastNode) {
                RevCommit curCommit;
                if (isLastNode) {
                    curCommit = RevCommitOpt.getRevCommitByObjectId(sinceObjectId, repo);
                    isLastNode = false;
                    if (!author.equals(curCommit.getAuthorIdent().getName())) continue;
                } else {
                    curCommit = iterator.next();
                    if (!iterator.hasNext()) isLastNode = true;
                }
                // 忽略特定提交
                if (isIgnoreShortMessage(curCommit.getShortMessage())) continue;
                // 重新获取 RevCommit 否则获取上一个提交会失败
                RevWalk walk = new RevWalk(repo);
                curCommit = walk.parseCommit(curCommit.getId());
                // 获取上一个提交
                RevCommit prevCommit = RevCommitOpt.getPrevCommit(curCommit, repo);
                if (prevCommit == null) {
                    System.out.println("\n" + DIVIDING_LINE + "\nCommitID: " + curCommit.getId().getName() + " not found PrevCommit");
                    continue;
                }
                System.out.println("\n" + DIVIDING_LINE + "\nRevisions new[" + curCommit.getId().getName() + "] between old[" + prevCommit.getId().getName() + "]");
                System.out.println("ShortMessage new[" + curCommit.getShortMessage() + "] between old[" + prevCommit.getShortMessage() + "]");
                Set<DiffFilesInCommit.ChangeFile> changeFileSet = DiffFilesInCommit.listDiffChangFile(repo, git, prevCommit.getId(), curCommit.getId());
                String commitDateStr = getCommitDateStr(curCommit);
                int count = 0;
                for (DiffFilesInCommit.ChangeFile c : changeFileSet) {
                    c.setCommitDateStr(commitDateStr);
                    String filePath = c.getNewPath();
                    if (c.getChangType() == DiffEntry.ChangeType.DELETE) filePath = c.getOldPath();
                    List<DiffFilesInCommit.ChangeFile> cfs = changFileHashMap.getOrDefault(filePath, new ArrayList<>());
                    cfs.add(c);
                    changFileHashMap.put(filePath, cfs);
                    System.out.println("<ChangType: " + c.getChangType().name() + "> " + c.getOldPath() + " >> " + c.getNewPath());
                    count++;
                }
                System.out.println("count = [" + count + "]");
                isLastNode = !iterator.hasNext();
            }
            changFileHashMap.keySet().forEach(key -> {
                List<DiffFilesInCommit.ChangeFile> changeFiles = changFileHashMap.get(key);
                // 按提交时间DESC 如果changType为DELETE则忽略
                changeFiles = changeFiles.stream()
                        .sorted(Comparator.comparing(DiffFilesInCommit.ChangeFile::getCommitDateStr).reversed())
                        .collect(Collectors.toList());
                if (changeFiles.get(0).getChangType() != DiffEntry.ChangeType.DELETE) {
                    changFileSet.add(changeFiles.get(0).getNewPath());
                }
            });
            return changFileSet;
        }
    }

    /**
     * 提交日志是否在忽略名单中
     *
     * @param shortMessage
     * @return
     */
    private static boolean isIgnoreShortMessage(String shortMessage) {
        final String[] ignoreShortMessages = new String[]{"Merge"};
        if (StringUtils.isBlank(shortMessage)) return false;
        for (String message : ignoreShortMessages) {
            if (shortMessage.startsWith(message)) return true;
        }
        return false;
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
        String gitDateTimeFormatString = "yyyy/MM/dd HH:mm:ss";
        return authorDateTime.format(DateTimeFormatter.ofPattern(gitDateTimeFormatString));
    }
}
