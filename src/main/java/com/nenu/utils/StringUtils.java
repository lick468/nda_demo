package com.nenu.utils;

public class StringUtils {
    public boolean isNull(String str){
        if(str==null || str.length()<=0){
            return true;
        }
        return false;
    }
}
