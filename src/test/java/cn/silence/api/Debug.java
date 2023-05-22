package cn.silence.api;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Debug {

    static List<DiffEntry> getChangedFileList(RevCommit revCommit, Repository repo) {
        List<DiffEntry> returnDiffs = null;
        try {
            RevCommit previsouCommit = getPrevHash(revCommit, repo);
            if (previsouCommit == null)
                return null;
            ObjectId head = revCommit.getTree().getId();

            ObjectId oldHead = previsouCommit.getTree().getId();

            System.out.println("Printing diff between the Revisions: " + revCommit.getName() + " and " + previsouCommit.getName());

            // prepare the two iterators to compute the diff between
            try (ObjectReader reader = repo.newObjectReader()) {
                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                oldTreeIter.reset(reader, oldHead);
                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                newTreeIter.reset(reader, head);

                // finally get the list of changed files
                try (Git git = new Git(repo)) {
                    List<DiffEntry> diffs = git.diff()
                            .setNewTree(newTreeIter)
                            .setOldTree(oldTreeIter)
                            .call();
                    for (DiffEntry entry : diffs) {
//                        System.out.println("Entry: " + entry);
                    }
                    returnDiffs = diffs;
                } catch (GitAPIException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return returnDiffs;
    }

    public static RevCommit getPrevHash(RevCommit commit, Repository repo) throws IOException {

        try (RevWalk walk = new RevWalk(repo)) {
            // Starting point
            walk.markStart(commit);
            int count = 0;
            for (RevCommit rev : walk) {
                // got the previous commit.
                if (count == 1) {
                    return rev;
                }
                count++;
            }
            walk.dispose();
        }
        //Reached end and no previous commits.
        return null;
    }


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String versionCommit = "8e4f5534";//需要分析的Commit Hash
        String path = "/Users/rainofsilence/.codes/ProjectsTMP/git-branch-simple";//对应项目在本地Repo的路径
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setMustExist(true);
        builder.addCeilingDirectory(new File(path));
        builder.findGitDir(new File(path));

        Repository repo;
        try {
            repo = builder.build();
            RevWalk walk = new RevWalk(repo);
            ObjectId versionId = repo.resolve(versionCommit);
            RevCommit verCommit = walk.parseCommit(versionId);
            List<DiffEntry> diffFix = getChangedFileList(verCommit, repo);
            for (DiffEntry entry : diffFix) {
                System.out.println(entry.getNewPath());
            }
//			RevWalk walk2 = new RevWalk(repo);
//			ObjectId versionId2=repo.resolve(versionCommit);
//			RevCommit verCommit2=walk2.parseCommit(versionId2);
//			List<DiffEntry> diffFix2=RunJGit.getChangedFileList(verCommit2,repo);
//			for (DiffEntry entry : diffFix2) {
//				System.out.println(entry.getNewPath());
//            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}