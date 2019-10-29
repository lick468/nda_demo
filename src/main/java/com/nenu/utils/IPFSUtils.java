package com.nenu.utils;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

public class IPFSUtils {
    private static final IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");

    public static String upload(String path) throws Exception{
        NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File(path));
        MerkleNode merkleNode = ipfs.add(file).get(0);
        return merkleNode.hash.toString();
    }

    public static String download(String hash) throws Exception {
        byte[] data = download2Bytes(hash);
        return new String(data);
    }

    public static byte[] download2Bytes(String hash) throws Exception {
        byte[] data = ipfs.cat(Multihash.fromBase58(hash));
        return data;
    }

    public static boolean download(String filePathName, String hash) throws IOException {
        Multihash filePointer = Multihash.fromBase58(hash);
        System.out.println("Here: download-35");
        List<MerkleNode> foundNodes = ipfs.ls(filePointer);
        if (foundNodes.isEmpty()) {
            System.out.println("File not found on server");
            return false;
        }
        byte[] data = ipfs.cat(filePointer);
        System.out.println("Data Len: " + data.length);
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
            System.out.println("Down len: " + data.length);
            return true;
        }
        else {
            return false;
        }
    }
}
