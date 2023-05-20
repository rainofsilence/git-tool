package cn.silence.api;

import junit.framework.TestCase;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

import java.io.IOException;

/**
 * @author rainofsilence
 * @version 1.0.0
 * @since 2023/05/20 16:28:32
 */
public class ApiTest extends TestCase {

    public void testPrintCommitEntryPathsTest() throws GitAPIException, IOException {
        ShowLog.printCommitEntryPaths(
                "",
                "",
                "",
                "");
    }

    public void testArchiveFilesInCommitTest() throws GitAPIException, IOException {
        Archive.archiveFilesInCommit(
                "",
                "",
                "",
                "",
                ""
        );
    }
}
