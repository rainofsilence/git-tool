package cn.silence.api;

import cn.silence.utils.Assert;
import cn.silence.utils.DateUtils;
import cn.silence.utils.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static cn.silence.api.ShowLog.getChangeFilePaths;

/**
 * @author rainofsilence
 * @version 1.0.0
 * @since 2023/05/20 16:11:34
 */
public class Archive {

    public static final String ARCHIVE = "archive";

    public static void archiveFilesInCommit(String localRepoPath, String author, String since, String until, String targetArchivePath) throws GitAPIException, IOException {
        Set<String> entryList = getChangeFilePaths(localRepoPath, author, since, until);
        if (targetArchivePath == null || targetArchivePath.length() == 0) {
            targetArchivePath = localRepoPath + File.separator + ARCHIVE + File.separator + ARCHIVE + "-" + DateUtils.formatDate();
        }
        FileUtils.createDir(targetArchivePath, true);
        for (String entryName : entryList) {
            FileUtils.copyFile(localRepoPath + File.separator + entryName, targetArchivePath + File.separator + entryName);
        }
        System.out.printf("Archive.archiveFilesInCommit success and entryList.size = [%s]%n", entryList.size());
    }
}
