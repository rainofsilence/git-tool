package cn.silence.api;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author rainofsilence
 * @version 1.0.0
 * @since 2023/05/20 15:58:05
 */
public class DiffFilesInCommit {


    /**
     * @param repo
     * @param git
     * @param oldCommit
     * @param newCommit
     * @return
     * @throws GitAPIException
     * @throws IOException
     */
    public static Set<CommitLog> listDiff(Repository repo, Git git, String oldCommit, String newCommit) throws GitAPIException, IOException {
        final List<DiffEntry> diffs = git.diff()
                .setOldTree(prepareTreeParser(repo, oldCommit))
                .setNewTree(prepareTreeParser(repo, newCommit))
                .call();
        Set<CommitLog> updateFileNameSet = new HashSet<>();
        for (DiffEntry diff : diffs) {
            CommitLog commitLog = new CommitLog();
            commitLog.setChangType(diff.getChangeType());
            commitLog.setPath(DiffEntry.ChangeType.DELETE == diff.getChangeType() ? diff.getOldPath() : diff.getNewPath());
            updateFileNameSet.add(commitLog);
        }
        return updateFileNameSet;
    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        // noinspection Duplicates
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }
}
