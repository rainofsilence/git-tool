package cn.silence.api;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.IOException;
import java.io.Serializable;
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
     * @param oldCommitId
     * @param newCommitId
     * @return
     * @throws GitAPIException
     * @throws IOException
     */
    public static Set<ChangeFile> listDiffChangFile(Repository repo, Git git, String oldCommitId, String newCommitId) throws GitAPIException, IOException {
        AbstractTreeIterator oldTree = prepareTreeParser(repo, oldCommitId);
        AbstractTreeIterator newTree = prepareTreeParser(repo, newCommitId);
        return listDiffChangFile(git, oldTree, newTree);
    }

    /**
     * @param repo
     * @param git
     * @param oldObjectId
     * @param newObjectId
     * @return
     * @throws GitAPIException
     * @throws IOException
     */
    public static Set<ChangeFile> listDiffChangFile(Repository repo, Git git, ObjectId oldObjectId, ObjectId newObjectId) throws GitAPIException, IOException {
        AbstractTreeIterator oldTree = prepareTreeParser(repo, oldObjectId);
        AbstractTreeIterator newTree = prepareTreeParser(repo, newObjectId);
        return listDiffChangFile(git, oldTree, newTree);
    }

    private static Set<ChangeFile> listDiffChangFile(Git git, AbstractTreeIterator oldTree, AbstractTreeIterator newTree) throws GitAPIException, IOException {
        final List<DiffEntry> diffs = git.diff()
                .setOldTree(oldTree)
                .setNewTree(newTree)
                .call();
        Set<ChangeFile> changeFileSet = new HashSet<>();
        for (DiffEntry diff : diffs) {
            ChangeFile changeFile = new ChangeFile();
            changeFile.setChangType(diff.getChangeType());
            changeFile.setOldPath(diff.getOldPath());
            changeFile.setNewPath(diff.getNewPath());
            changeFileSet.add(changeFile);
        }
        return changeFileSet;
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

    private static AbstractTreeIterator prepareTreeParser(Repository repository, ObjectId objectId) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        // noinspection Duplicates
        try (RevWalk walk = new RevWalk(repository)) {
            RevTree tree = walk.parseTree(objectId);
            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }
            walk.dispose();
            return treeParser;
        }
    }

    /**
     * @author rainofsilence
     * @version 1.0.0
     * @since 2023/05/20 17:55:38
     */
    public static class ChangeFile implements Serializable {

        private static final long serialVersionUID = -6154132705858157795L;
        private String oldPath;
        private String newPath;
        private DiffEntry.ChangeType changType;
        private String commitDateStr;

        public String getOldPath() {
            return oldPath;
        }

        public void setOldPath(String oldPath) {
            this.oldPath = oldPath;
        }

        public String getNewPath() {
            return newPath;
        }

        public void setNewPath(String newPath) {
            this.newPath = newPath;
        }

        public DiffEntry.ChangeType getChangType() {
            return changType;
        }

        public void setChangType(DiffEntry.ChangeType changType) {
            this.changType = changType;
        }

        public String getCommitDateStr() {
            return commitDateStr;
        }

        public void setCommitDateStr(String commitDateStr) {
            this.commitDateStr = commitDateStr;
        }

        @Override
        public String toString() {
            return "ChangeFile{" +
                    "oldPath='" + oldPath + '\'' +
                    ", newPath='" + newPath + '\'' +
                    ", changType=" + changType +
                    ", commitDateStr='" + commitDateStr + '\'' +
                    '}';
        }
    }
}
