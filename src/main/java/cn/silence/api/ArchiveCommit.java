package cn.silence.api;

import cn.silence.utils.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static cn.silence.api.ShowLog.getCommitEntryList;

/**
 * @author rainofsilence
 * @version 1.0.0
 * @since 2023/05/20 16:11:34
 */
public class ArchiveCommit {

    public static void archiveFilesInCommit(String localRepoPath, String author, String since, String until, String targetArchivePath) throws GitAPIException, IOException {
        Set<String> entryList = getCommitEntryList(localRepoPath, author, since, until);
        boolean dir = FileUtils.createDir(targetArchivePath, true);
        entryList.forEach(e->{
            FileUtils.createFile(targetArchivePath + File.separator + e);
        });
    }
}
