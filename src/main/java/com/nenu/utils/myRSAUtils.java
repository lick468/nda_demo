package com.nenu.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.security.*;

/*Author: Sunct
* Date: 2019.11.29
* 基于RSAUtils做部分扩展，包括自定义函数，和基于原因函数的改造*/
public class myRSAUtils extends RSAUtils {
    
    public static void myEncryptFile(String inputPath, String outputPath, String publicKeyStr, boolean writeKey2File) throws Exception {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecureRandom random = new SecureRandom();
            keygen.init(random);
            SecretKey key = keygen.generateKey();
            DataOutputStream out = new DataOutputStream(new FileOutputStream(outputPath));
            Cipher cipher;
            if (writeKey2File) {
                PublicKey publicKey = getPublicKey(publicKeyStr);
                cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.WRAP_MODE, publicKey);
                byte[] wrappedKey = cipher.wrap(key);
                out.writeInt(wrappedKey.length);
                out.write(wrappedKey);
            }
            InputStream in = new FileInputStream(inputPath);
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            crypt(in, out, cipher);
            in.close();
            out.close();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    //************************文件加密，数据保存到数组**************************
    public static void myEncryptStream(InputStream in, OutputStream out, String publicKeyStr, boolean writeKey2Stream) throws Exception {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecureRandom random = new SecureRandom();
            keygen.init(random);
            SecretKey key = keygen.generateKey();
            Cipher cipher;
            if (writeKey2Stream) {
                PublicKey publicKey = getPublicKey(publicKeyStr);
                cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.WRAP_MODE, publicKey);
                byte[] wrappedKey = cipher.wrap(key);
                out.write(Convert.int2ByteArray(wrappedKey.length));
                //System.out.println("wrappedKey.length written to file: " + wrappedKey.length);
                //System.out.println("Wrapped key:" + Convert.byteArrayToHexStr(wrappedKey));
                out.write(wrappedKey);
            }
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            crypt(in, out, cipher);
            out.flush();
            //in.close();
            //out.close();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public static void myDecryptBytes2Stream(@NotNull byte[] inData, OutputStream out, String privateKeyStr) throws Exception {
        try {
            byte[] keyLength = new byte[4];
            System.arraycopy(inData, 0, keyLength, 0, 4);
            int length = Convert.byteArray2Int(keyLength);//in.readInt();
            //System.out.println("wrappedKey.length read from file: " + length);
            byte[] wrappedKey = new byte[length];
            System.arraycopy(inData, 4, wrappedKey, 0, length);
            PrivateKey privateKey = getPrivateKey(privateKeyStr);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.UNWRAP_MODE, privateKey);
            Key key = cipher.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);
            
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            
            myCryptByteArray(inData, length + 4, out, cipher);//, Math.min(524288, inData.length - length - 4));
            out.flush();
            //out.close();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    //************************解密文件**************************
    /*
     * Sunct, 2019.10.28
     * 实现之间在内存之间对数据进行解密
     */
    public static void myDecryptBytes2StreamUsingStream(byte[] inData, OutputStream out, String privateKeyStr) throws Exception {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(inData);
            byte[] keyLength = new byte[4];
            in.read(keyLength, 0, 4);
            int length = Convert.byteArray2Int(keyLength);//in.readInt();
            //System.out.println("wrappedKey.length read from file: " + length);
            byte[] wrappedKey = new byte[length];
            in.read(wrappedKey, 0, length);
            PrivateKey privateKey = getPrivateKey(privateKeyStr);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.UNWRAP_MODE, privateKey);
            Key key = cipher.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);
            
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            
            crypt(in, out, cipher);
            out.flush();
            in.close();
            //out.close();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /*
     * Sunct, 2019.11.02
     * 在原有crypt函数基础上，实现对byte数组的加密，而且直接从byte数组输出*/
    //对数据分段加密解密
    public static void myCrypt(@NotNull byte[] inByteArray, @NotNull byte[] outByteArray, Cipher cipher) throws IOException, GeneralSecurityException {
        int blockSize = cipher.getBlockSize();
        int outputSize = cipher.getOutputSize(blockSize);
        byte[] inBytes = new byte[blockSize];
        byte[] outBytes = new byte[outputSize];
        
        int inLength = 0;
        boolean next = true;
        int curInPos = 0;
        int totalInLen = inByteArray.length;
        int curOutPos = 0;
        //System.out.println("File content>>>");
        while (next) {
            inLength = Math.min(blockSize, totalInLen - curInPos);
            if (inLength == blockSize) {
                int outLength = cipher.update(inByteArray, curInPos, blockSize, outBytes);
                System.arraycopy(outBytes, 0, outByteArray, curOutPos, outLength);
                curInPos += inLength;
                curOutPos += outLength;
            } else {
                next = false;
            }
        }
        if (inLength > 0) {
            outBytes = cipher.doFinal(inByteArray, curInPos, inLength);
        } else {
            outBytes = cipher.doFinal();
        }
        System.arraycopy(outBytes, 0, outByteArray, curOutPos, outputSize);
    }
    
    public static void myCryptByteArray(@NotNull byte[] inByteArray, int offset, OutputStream out, Cipher cipher) throws IOException, GeneralSecurityException {
        int totalInLen = inByteArray.length;
        myCryptByteArray(inByteArray, offset, out, cipher, totalInLen);
    }
    
    public static void myCryptByteArray(@NotNull byte[] inByteArray, int offset, OutputStream out, Cipher cipher, int dataLen) throws IOException, GeneralSecurityException {
        int blockSize = cipher.getBlockSize();
        int outputSize = cipher.getOutputSize(blockSize);
        byte[] inBytes = new byte[blockSize];
        byte[] outBytes = new byte[outputSize];
        
        int inLength = 0;
        boolean next = true;
        int curInPos = offset;
        //System.out.println("File content>>>");
        while (next) {
            inLength = Math.min(blockSize, dataLen - curInPos);
            if (inLength == blockSize) {
                int outLength = cipher.update(inByteArray, curInPos, blockSize, outBytes);
                out.write(outBytes, 0, outLength);
                curInPos += inLength;
            } else {
                next = false;
            }
        }
        if (inLength > 0) {
            outBytes = cipher.doFinal(inByteArray, curInPos, inLength);
        } else {
            outBytes = cipher.doFinal();
        }
        out.write(outBytes);
    }
    
    /*
     * Sunct, 2019.11.02
     * 通过public key string 获得cipher和wrapped key
     * */
    public Cipher myGetPublicKeyCipher(@NotNull String publicKeyStr, byte[] wrappedKey) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA");
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom();
        keygen.init(random);
        SecretKey key = keygen.generateKey();
        PublicKey publicKey = getPublicKey(publicKeyStr);
        cipher.init(Cipher.WRAP_MODE, publicKey);
        wrappedKey = cipher.wrap(key);
        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher;
    }
    
    /*
     * 利用private key string 和相关信息得到cipher
     * Sunct，2019.11.02
     * */
    public Cipher myGetPrivateKeyCipher(String privateKeyStr, byte[] wrappedKey) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA");
        try {
            PrivateKey privateKey = getPrivateKey(privateKeyStr);
            cipher.init(Cipher.UNWRAP_MODE, privateKey);
            Key key = cipher.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);
            
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            //return cipher;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cipher;
    }

}
