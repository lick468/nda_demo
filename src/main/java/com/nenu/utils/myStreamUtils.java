package com.nenu.utils;

import java.io.IOException;
import java.io.InputStream;

public class myStreamUtils {
    public static int getInputStreamLen(InputStream in) {
        int blockSize = 1024 * 1024 * 4;
        int len = 0;
        int curReadLen = 0;
        byte[] buf = new byte[blockSize];
        //in.mark(0);
        try {
            while ((curReadLen = in.read(buf, 0, blockSize)) > 0) {
                len += curReadLen;
            }
            //in.reset();//保证后面能正确读内容
            return len;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
