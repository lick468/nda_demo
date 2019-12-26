package com.nenu.constant;

import java.io.File;

public final class FileDirConst {
    public static final String FILESEPARATOR = File.separator;
    public static final String NDA_ROOTDIR = "C:\\ndadata\\";
    public static final String DEFAULT_PREFIX_PDFDOWNPATH_PASS = NDA_ROOTDIR + "download\\outpath\\";
    public static final String DEFAULT_PREFIX_PDFDOWNPATH_DECRPT = NDA_ROOTDIR + "download\\decpath\\";
    public static final String DEFAULT_TMPPDFFILENAME = "tmpfile.pdf";
    public static final String DEFAULT_FILEEXTENSION = ".pdf";
    public static final String DEFAULT_PREFIX_UPPATH_PASS = NDA_ROOTDIR + "upload\\outpath\\";
    public static final String DEFAULT_PREFIX_UPPATH_ORG = NDA_ROOTDIR + "upload\\orgpath\\";
    public static final String DEFAULT_PREFIX_UPPATH_NDADOC = NDA_ROOTDIR + "upload\\ndadoc\\";
    public static final String DEFAULT_SUFFIX_NDADOC_ORG = "_org";
    public static final String DEFAULT_SUFFIX_NDADOC_PASS = "_pass";
}
