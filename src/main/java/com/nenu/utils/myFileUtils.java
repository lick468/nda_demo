package com.nenu.utils;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class myFileUtils {
    public static boolean deleteFiles(List<String> fileNameList) {
        if (null == fileNameList || fileNameList.isEmpty())
            return true;
        try {
            for (String curFileName : fileNameList) {
                File curFile = new File(curFileName);
                if (curFile.exists() && curFile.isFile()) {
                    curFile.delete();
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /*
    * 新建文件，如果文件所在路径不存在，则首先创建路径
    * */
    public static boolean CreateFilewithDir(String filename) throws IOException{
        boolean createDirSuccess = false;
        File file = new File(filename);
        if(file.exists()){
            return true;
        } else {
            File parentDirFile = file.getParentFile();
            if (parentDirFile.exists()) {
                createDirSuccess = true;
            } else {
                if (parentDirFile.mkdirs()) {
                    createDirSuccess = true;
                }
            }
            if (createDirSuccess) {
                return file.createNewFile();
            }
            else {
                return false;
            }
        }
    }

    public static boolean DeleteFilewithParentDir(String filepath) {
        return DeleteFilewithParentDir(filepath, true);
    }
    /*
    * 删除文件，同时删除其上级目录(根据参数)
    * */
    public static boolean DeleteFilewithParentDir(String filepath, boolean deleteparent) {
        if (null == filepath || "".equalsIgnoreCase(filepath))
            return false;
        try {
            File curFile = new File(filepath);
            File parentDirFile = curFile.getParentFile();
            String parentDir = curFile.getParent();
            if (curFile.isFile() && curFile.exists()) {
                curFile.delete();
            }
            if (deleteparent) {
                if (parentDir.lastIndexOf(":\\") == parentDir.length() - 2) {
                    return true;
                } else {
                    return parentDirFile.delete();
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            System.out.println("删除文件出错： " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteFile(@NotNull String filename) {
        File file = new File(filename);
        if (file.exists())
            return file.delete();
        return true;
    }
}
