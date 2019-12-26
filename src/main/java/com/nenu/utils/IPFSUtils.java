package com.nenu.utils;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

public class IPFSUtils {
    private static final IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");

    public static String upload(String path) throws Exception{
        NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File(path));
        MerkleNode merkleNode = ipfs.add(file).get(0);
        return merkleNode.hash.toString();
    }

    /*
    * Sunct, 2019.10.30
    * 之间将数组传输到ipfs服务器，减少文件读写
     */
    public static String upload(byte[] data) throws Exception{
        NamedStreamable.ByteArrayWrapper file = new NamedStreamable.ByteArrayWrapper(data);
        MerkleNode merkleNode = ipfs.add(file).get(0);
        return merkleNode.hash.toString();
    }

    public static String download(String hash) throws Exception {
        byte[] data = download2Bytes(hash);
        return new String(data);
    }

    public static byte[] download2Bytes(String hash) throws IOException {
        // hash IPFS上传文件返回的hash,是经过IPFS加密后的hash，所以如果输入的hash不对的话，就会报错
        // Multiformat协议之一是Multihash  是面向未来的加密hash
        byte[] data = new byte[1];
        data[0] = 0;
        //Multihash filePointer = null;
        try {
            //filePointer = Multihash.fromBase58(hash);
            //data = ipfs.cat(Multihash.fromBase58(hash));
            data = myGetIpfsFile(ipfs, Multihash.fromBase58(hash));
        }catch (Exception e) {
            //System.out.println("Bad hash to retrieve from server");
            //e.printStackTrace();
            throw new IOException("Bad hash to retrieve from server");
            //return data;
        }
        return data;
    }

    public static byte[] myGetIpfsFile(IPFS ipfs, Multihash hash) throws  IOException{
        String path = "cat?arg=" + hash;
        int blockSize = 8192;
        URL target = new URL(ipfs.protocol, ipfs.host, ipfs.port, "/api/v0/" + path);

        HttpURLConnection conn = (HttpURLConnection)target.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");

        try {
            /*read(byte[] buffer):
            * InputStream中是一个字节一个字节循环读
            * 而BufferedInputStream中是利用System.ArrayCopy*/
            BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
            ByteArrayOutputStream resp = new ByteArrayOutputStream();
            byte[] buf = new byte[blockSize];
            //in.reset();
            int inputLen = myStreamUtils.getInputStreamLen(in);
            //System.out.println("InputLen: " + inputLen);
            if (inputLen <= 0)
                return null;
            byte[] outBuffer = new byte[inputLen];
            int posOut = 0;
            in.close();
            conn.disconnect();
            conn = (HttpURLConnection)target.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            in = new BufferedInputStream(conn.getInputStream());

            int r;
            while((r = in.read(buf)) > 0) {
                //resp.write(buf, 0, r);
                //System.out.println("Current output position: " + posOut);
                System.arraycopy(buf, 0, outBuffer, posOut, r);

                posOut += r;
            }
            in.close();
            //return resp.toByteArray();
            return outBuffer;
        } catch (ConnectException var6) {
            throw new RuntimeException("Couldn't connect to IPFS daemon at " + target + "\n Is IPFS running?");
        } catch (IOException var7) {
            var7.printStackTrace();
            //String err = new String(readFully(conn.getErrorStream()));
            throw new RuntimeException("IOException contacting IPFS daemon.\nTrailer: " + conn.getHeaderFields().get("Trailer"), var7); //+ " " + err
        }

    }

    private static final byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream resp = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];

        int r;
        while((r = in.read(buf)) >= 0) {
            resp.write(buf, 0, r);
        }

        return resp.toByteArray();
    }

    public static boolean download(String filePathName, String hash) throws Exception {
        byte[] data = null;
        try {
            data = download2Bytes(hash);
        } catch (IOException e) {
            throw new IOException(e);
        }
        //System.out.println("Data Len: " + data.length);
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
            //System.out.println("Down len: " + data.length);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 检查本机的5001 端口是否开启
     * @return
     */
    public static boolean checkIpfsRunning(@NotNull String serverIP, int port) {
        Socket socket = null;

        try {
            socket = new Socket("127.0.0.1", 5001);
            socket = null;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean checkIpfsRunning(@NotNull String serverIP) {
        return checkIpfsRunning(serverIP, 5001);
    }

    public static boolean checkIpfsRunning() {
        return checkIpfsRunning("127.0.0.1", 5001);
    }

}
