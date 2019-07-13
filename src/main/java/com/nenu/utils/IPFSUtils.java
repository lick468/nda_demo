package com.nenu.utils;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class IPFSUtils {
    private static final IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");

    public static String upload(String path) throws Exception{
        NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File(path));
        MerkleNode merkleNode = ipfs.add(file).get(0);
        return merkleNode.hash.toString();
    }

    public static String download(String hash, HttpServletResponse response) throws Exception {
        byte[] data = ipfs.cat(Multihash.fromBase58(hash));
        InputStream inputStream=new ByteArrayInputStream(data);
        OutputStream os = response.getOutputStream();
        byte[] buffer = new byte[400];
        int length = 0;
        while ((length = inputStream.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        os.flush();
        os.close();
        return new String(data);
    }
    public static void download(String filePathName,String hash) throws IOException {
        Multihash filePointer = Multihash.fromBase58(hash);
        byte[] data = ipfs.cat(filePointer);
        if(data != null){
            String path = filePathName.substring(0, filePathName.lastIndexOf("\\"));
            String fileName = filePathName.substring(filePathName.lastIndexOf("\\")+1,filePathName.length()); //文件名后缀  E:\test.doc  doc
            File file = new File(path,fileName);
            if(file.exists()){
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data,0,data.length);
            fos.flush();
            fos.close();
        }
    }
}
