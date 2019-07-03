package com.nenu.controller;

import io.ipfs.api.IPFS;
import io.ipfs.api.NamedStreamable;
import io.ipfs.api.MerkleNode;
import io.ipfs.multihash.Multihash;
import java.io.*;
public class IPFSTest {

    static IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");//ipfs的服务器地址和端口
    public static void main(String[] args) {
        System.out.println("Hello World!");
        try {
            System.out.println(upload("D:\\workShop\\IDEA_work\\test2.pdf"));  //upload
            //download("d:\\1.pdf","QmZH3sa4EpHNM8bQbp3EJ65LWDDUvCYT8LRZRFK9DtVQhi");  //download
        }
        catch(Exception e) {
            System.out.println("IPFS error!");
        }
    }

    public static String upload(String filePathName) throws IOException {
        //filePathName指的是文件的上传路径+文件名，如D:/1.png  
        NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File(filePathName));
        MerkleNode addResult = ipfs.add(file).get(0);
        return addResult.hash.toString();
    }

    public static void download(String filePathName,String hash) throws IOException {
        Multihash filePointer = Multihash.fromBase58(hash);
        byte[] data = ipfs.cat(filePointer);
        if(data != null){
            File file = new File(filePathName);
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

