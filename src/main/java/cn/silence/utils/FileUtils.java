package cn.silence.utils;

import java.io.File;
import java.nio.file.Files;

/**
 * @author rainofsilence
 * @version 1.0.0
 * @since 2023/05/20 16:15:33
 */
public class FileUtils {

    public static boolean createFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) return true;
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) parentDir.mkdirs();
        try {
            return file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createDir(String dirPath) {
        return createDir(dirPath, false);
    }

    public static boolean createDir(String dirPath, boolean overwrite) {
        File dir = new File(dirPath);
        if (overwrite && dir.exists()) {
            deleteFile(dir);
        }
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return true;
    }

    public static boolean deleteFile(File file) {
        if (!file.exists()) return true;
        if (file.isDirectory()) {
            String[] children = file.list();
            assert children != null;
            for (String child : children) {
                deleteFile(new File(file, child));
            }
        }
        return file.delete();
    }

    /**
     * @param source
     * @param target
     * @return
     */
    public static boolean copyFile(String source, String target) {
        try {
            File targetFile = new File(target);
            File parentDir = targetFile.getParentFile();
            if (!parentDir.exists()) parentDir.mkdirs();
            Files.copy(new File(source).toPath(), new File(target).toPath());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
