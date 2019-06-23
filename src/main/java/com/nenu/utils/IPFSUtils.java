package com.nenu.utils;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class IPFSUtils {
    private static final IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");

    public static String upload() throws Exception{
        NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File("D://code.doc"));
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
}
