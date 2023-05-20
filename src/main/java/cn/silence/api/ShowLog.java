package cn.silence.api;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
    public static void getCommitEntryListPrint(String localRepoPath, String author, String since, String until) throws IOException, GitAPIException {
        Set<String> entryList = getCommitEntryList(localRepoPath, author, since, until);
        entryList.forEach(System.out::println);
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
    public static Set<String> getCommitEntryList(String localRepoPath, String author, String since, String until) throws IOException, GitAPIException {
        try (Git git = Git.open(new File(localRepoPath))) {
            Repository repo = git.getRepository();
            Iterable<RevCommit> logs = git.log().addRange(repo.resolve(since), repo.resolve(until)).call();
            Iterator<RevCommit> iterator = logs.iterator();
            RevCommit old;
            RevCommit last = null;
            Set<String> updateFileNameSet = new HashSet<>();
            while (iterator.hasNext()) {
                if (last == null) {
                    last = iterator.next();
                    continue;
                }
                if (!last.getAuthorIdent().getName().contains(author)) {
                    last = null;
                    continue;
                }
                if (iterator.hasNext()) {
                    old = iterator.next();
                    Set<String> curFileNameSet = DiffFilesInCommit.listDiffNotDelete(repo, git, old.getId().getName(), last.getId().getName());
                    updateFileNameSet.addAll(curFileNameSet);
                    if (old.getAuthorIdent().getName().contains(author)) {
                        last = old;
                        continue;
                    }
                    last = null;
                }
            }
            // 排序
            Set<String> sortSet = new TreeSet<>(Comparator.reverseOrder());
            sortSet.addAll(updateFileNameSet);
            return sortSet;
        }
    }
}
