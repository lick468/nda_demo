package com.nenu.utils;

import javax.crypto.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.security.*;

/* 利用CipherInputStream CipherOutputStream之间对数据进行加密解密
* */
public class myCipherUtilswithCipherStream {
    public static void encryptFilewithCipherStream(String inputPath, String outputPath, String publicKeyStr) throws Exception {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecureRandom random = new SecureRandom();
            keygen.init(random);
            SecretKey key = keygen.generateKey();
            PublicKey publicKey = RSAUtils.getPublicKey(publicKeyStr);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.WRAP_MODE, publicKey);
            byte[] wrappedKey = cipher.wrap(key);
            DataOutputStream outStream = new DataOutputStream(new FileOutputStream(outputPath));
            outStream.writeInt(wrappedKey.length);
            outStream.write(wrappedKey);
            InputStream inStream = new FileInputStream(inputPath);
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encryptStreamwithCipherStream(inStream, outStream, cipher);
            inStream.close();
            outStream.close();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void encryptStreamwithCipherStream(InputStream inStream, OutputStream outStream, Cipher cipher) throws Exception {
        CipherOutputStream coStream = new CipherOutputStream(outStream, cipher);
        int blockSize = cipher.getBlockSize();
        byte[] inBytes = new byte[blockSize];

        int inLength = 0;
        boolean next = true;
        while (next) {
            inLength = inStream.read(inBytes, 0, blockSize);
            if (inLength > 0) {
                coStream.write(inBytes, 0, inLength);
            }
            if (inLength < blockSize) {
                next = false;
            }
        }
        coStream.flush();
        coStream.close();
    }

    public static void encryptByteswithCipherStream(@NotNull @NotEmpty byte[] bytesIn, @NotNull OutputStream outStream, @NotNull Cipher cipher) throws Exception {
        CipherOutputStream coStream = new CipherOutputStream(outStream, cipher);
        int blockSize = cipher.getBlockSize();
        byte[] inBytes = new byte[blockSize];

        int inTotalLen = bytesIn.length;
        int inLength = 0;
        int offset = 0;
        while (offset < inTotalLen) {
            inLength = Math.min(inTotalLen - offset, blockSize);
            if (inLength > 0) {
                coStream.write(inBytes, offset, inLength);
            }
            offset += inLength;
        }
        coStream.flush();
        coStream.close();
    }

    public static void decryptData1(@NotNull @NotEmpty byte[] inData, OutputStream outStream, String privateKeyStr) throws Exception {
        try {
            byte[] keyLength = new byte[4];
            System.arraycopy(inData, 0, keyLength, 0, 4);
            int length = Convert.byteArray2Int(keyLength);//in.readInt();
            //System.out.println("wrappedKey.length read from file: " + length);
            byte[] wrappedKey = new byte[length];
            System.arraycopy(inData, 4, wrappedKey, 0, length);
            PrivateKey privateKey = RSAUtils.getPrivateKey(privateKeyStr);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.UNWRAP_MODE, privateKey);
            Key key = cipher.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);

            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            ByteArrayInputStream biStream = new ByteArrayInputStream(inData, length + 4, inData.length - length - 4);
            DecryptwithCipherInStream(biStream, outStream, cipher);
            //out.close();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private  static void DecryptwithCipherInStream(@NotNull InputStream inputStream, @NotNull OutputStream oStream, @NotNull Cipher cipher) throws Exception{
        CipherInputStream ciStream = new CipherInputStream(inputStream, cipher);
        int blockSize = cipher.getBlockSize();
        byte[] inData = new byte[blockSize];
        int readLen = 0;
        while (true) {
            readLen = ciStream.read(inData, 0, blockSize);
            if (readLen > 0) {
                oStream.write(inData, 0, readLen);
            } else {
                break;
            }
        }
        oStream.flush();
        ciStream.close();
    }

}
