package cn.silence.api;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;

/**
 * @author rainofsilence
 * @version 1.0.0
 * @since 2023/05/22 21:12:25
 */
public class RevCommitOpt {

    /**
     * 获取RevCommit的上一个节点
     *
     * @param commit
     * @param repository
     * @return
     * @throws IOException
     */
    public static RevCommit getPrevCommit(RevCommit commit, Repository repository) throws IOException {

        try (RevWalk walk = new RevWalk(repository)) {
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
        // Reached end and no previous commits.
        return null;
    }

    /**
     * @param objectId
     * @param repository
     * @return
     * @throws IOException
     */
    public static RevCommit getRevCommitByObjectId(ObjectId objectId, Repository repository) throws IOException {
        try (RevWalk walk = new RevWalk(repository)) {
            return walk.parseCommit(objectId);
        }
    }
}
