package com.nenu.utils;

import java.io.File;
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
}
