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

    public void testGetCommitEntryListTest() throws GitAPIException, IOException {
        ShowLog.getCommitEntryListPrint(
                "",
                "",
                "",
                "");
		System.out.println("ShowLog.getCommitEntryListPrint success!");
    }

    public void testArchiveFilesInCommitTest() throws GitAPIException, IOException {
        ArchiveCommit.archiveFilesInCommit(
                "",
                "",
                "",
                "",
                ""
        );
        System.out.println("ArchiveCommit.archiveFilesInCommit success!");
    }
}
