package cn.silence.api;

import junit.framework.TestCase;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

/**
 * @author rainofsilence
 * @version 1.0.0
 * @since 2023/05/20 16:28:32
 */
public class ApiTest extends TestCase {

    public void testPrintCommitEntryPathsTest() throws GitAPIException, IOException {
        ShowLog.printChangeFilePaths(
                "C:\\Users\\rainofsilence\\Codes\\TMP\\butterfly",
                "rainofsilence",
                "6ce29582",
                "46b11f71");
    }

    public void testArchiveFilesInCommitTest() throws GitAPIException, IOException {
        Archive.archiveFilesInCommit(
                "C:\\Users\\rainofsilence\\Codes\\TMP\\butterfly",
                "rainofsilence",
                "6ce29582",
                "46b11f71",
                ""
        );
    }
}
