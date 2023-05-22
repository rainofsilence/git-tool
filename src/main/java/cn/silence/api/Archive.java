package cn.silence.api;

import cn.silence.utils.DateUtils;
import cn.silence.utils.FileUtils;
import cn.silence.utils.StringUtils;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static cn.silence.api.ShowLog.getChangeFilePaths;
import static cn.silence.utils.StrLenConstant.DIVIDING_LINE;

/**
 * @author rainofsilence
 * @version 1.0.0
 * @since 2023/05/20 16:11:34
 */
public class Archive {

    public static final String ARCHIVE = "archive";

    public static void archiveFilesInCommit(String localRepoPath, String author, String since, String until, String targetArchivePath) throws GitAPIException, IOException {
        Set<String> entryList = getChangeFilePaths(localRepoPath, author, since, until);
        String repoName;
        File dir = new File(localRepoPath);
        if (dir.getName().equals(".git")) {
            File parentFile = dir.getParentFile();
            repoName = parentFile.getName();
            localRepoPath = parentFile.getPath();
        } else {
            repoName = dir.getName();
            localRepoPath = dir.getPath();
        }

        if (StringUtils.isBlank(targetArchivePath)) {
            targetArchivePath = localRepoPath + File.separator + ARCHIVE + "_" + repoName + File.separator + ARCHIVE + "_" + DateUtils.formatDate();
        } else {
            targetArchivePath = targetArchivePath + File.separator + ARCHIVE + "_" + repoName + File.separator + ARCHIVE + "_" + DateUtils.formatDate();
        }
        FileUtils.createDir(targetArchivePath, true);
        int count = 0;
        System.out.println("\nprint final changFile list\n" + DIVIDING_LINE);
        for (String entryName : entryList) {
            System.out.println(entryName);
            FileUtils.copyFile(localRepoPath + File.separator + entryName, targetArchivePath + File.separator + entryName);
            count++;
        }
        System.out.println("\nOutputPath: " + targetArchivePath + "\nChangeFile size = [" + count + "]");
    }
}
