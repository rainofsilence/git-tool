package cn.silence.api;

import org.eclipse.jgit.diff.DiffEntry;

import java.time.LocalDateTime;

/**
 * @author rainofsilence
 * @version 1.0.0
 * @since 2023/05/20 17:55:38
 */
public class CommitLog {

    private String path;

    private DiffEntry.ChangeType changType;

    private String commitDateStr;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
        return "CommitLog{" +
                "path='" + path + '\'' +
                ", changType=" + changType +
                ", commitDateStr='" + commitDateStr + '\'' +
                '}';
    }
}
