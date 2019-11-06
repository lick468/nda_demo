package com.nenu.utils;



import org.apache.commons.codec.binary.Base64;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAUtils {

    public static final String KEY_ALGORITHM = "RSA";
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";
    public static final String SIGNATURE_ALGORITHM="MD5withRSA";
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static Map<String, Object> initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator
                .getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }
    //获得公钥字符串
    public static String getPublicKeyStr(Map<String, Object> keyMap) throws Exception {
        //获得map中的公钥对象 转为key对象
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        //编码返回字符串
        return encryptBASE64(key.getEncoded());
    }


    //获得私钥字符串
    public static String getPrivateKeyStr(Map<String, Object> keyMap) throws Exception {
        //获得map中的私钥对象 转为key对象
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        //编码返回字符串
        return encryptBASE64(key.getEncoded());
    }

    //获取公钥
    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        // keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        keyBytes = Base64.decodeBase64(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    //获取私钥
    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        //  keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        keyBytes = Base64.decodeBase64(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    //解码返回byte
    public static byte[] decryptBASE64(String key) throws Exception {
        // return (new BASE64Decoder()).decodeBuffer(key);
        return Base64.decodeBase64(key);
    }


    //编码返回字符串
    public static String encryptBASE64(byte[] key) throws Exception {
        // return (new BASE64Encoder()).encodeBuffer(key);
        return Base64.encodeBase64String(key);
    }

    //***************************签名和验证*******************************
    public static byte[] sign(byte[] data,String privateKeyStr) throws Exception{
        PrivateKey priK = getPrivateKey(privateKeyStr);
        Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
        sig.initSign(priK);
        sig.update(data);
        return sig.sign();
    }

    public static boolean verify(byte[] data,byte[] sign,String publicKeyStr) throws Exception{
        PublicKey pubK = getPublicKey(publicKeyStr);
        Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
        sig.initVerify(pubK);
        sig.update(data);
        return sig.verify(sign);
    }

    //************************加密解密**************************
    public static byte[] encrypt(byte[] plainText,String publicKeyStr)throws Exception{
        PublicKey publicKey = getPublicKey(publicKeyStr);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int inputLen = plainText.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        int i = 0;
        byte[] cache;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(plainText, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(plainText, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptText = out.toByteArray();
        out.close();
        return encryptText;
    }

    public static byte[] decrypt(byte[] encryptText, String privateKeyStr)throws Exception{
        PrivateKey privateKey = getPrivateKey(privateKeyStr);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        int inputLen = encryptText.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptText, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptText, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] plainText = out.toByteArray();
        out.close();
        return plainText;
    }
    //************************加密文件**************************
    public static void encryptFile(String inputPath, String outputPath, String publicKeyStr) throws Exception {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecureRandom random = new SecureRandom();
            keygen.init(random);
            SecretKey key = keygen.generateKey();
            PublicKey publicKey = getPublicKey(publicKeyStr);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.WRAP_MODE, publicKey);
            byte[] wrappedKey = cipher.wrap(key);
            DataOutputStream out = new DataOutputStream(new FileOutputStream(outputPath));
            out.writeInt(wrappedKey.length);
            out.write(wrappedKey);
            InputStream in = new FileInputStream(inputPath);
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            crypt(in, out, cipher);
            in.close();
            out.close();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //************************文件加密，数据保存到数组**************************
    public static void encryptData(InputStream in, OutputStream out, String publicKeyStr) throws Exception {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecureRandom random = new SecureRandom();
            keygen.init(random);
            SecretKey key = keygen.generateKey();
            PublicKey publicKey = getPublicKey(publicKeyStr);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.WRAP_MODE, publicKey);
            byte[] wrappedKey = cipher.wrap(key);
            out.write(Convert.int2ByteArray(wrappedKey.length));
            //System.out.println("wrappedKey.length written to file: " + wrappedKey.length);
            //System.out.println("Wrapped key:" + Convert.byteArrayToHexStr(wrappedKey));
            out.write(wrappedKey);
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            crypt(in, out, cipher);
            out.flush();
            //in.close();
           //out.close();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //************************解密文件**************************
    public static void decryptFile(String inputPath, String outputPath, String privateKeyStr) throws Exception {
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(inputPath));
            int length = in.readInt();
            byte[] wrappedKey = new byte[length];
            in.read(wrappedKey, 0, length);
            PrivateKey privateKey = getPrivateKey(privateKeyStr);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.UNWRAP_MODE, privateKey);
            Key key = cipher.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);

            OutputStream out = new FileOutputStream(outputPath);
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);

            crypt(in, out, cipher);
            in.close();
            out.close();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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


    //************************解密文件**************************
    /*
    * Sunct, 2019.10.28
    * 实现之间在内存之间对数据进行解密
     */
    public static void decryptFile1(byte[] inData, OutputStream out, String privateKeyStr) throws Exception {
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

    public static void decryptData1(@NotNull byte[] inData, OutputStream out, String privateKeyStr) throws Exception {
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

    //对数据分段加密解密
    public static void crypt(InputStream in, OutputStream out, Cipher cipher) throws IOException, GeneralSecurityException {
        int blockSize = cipher.getBlockSize();
        int outputSize = cipher.getOutputSize(blockSize);
        byte[] inBytes = new byte[blockSize];
        byte[] outBytes = new byte[outputSize];

        int inLength = 0;
        boolean next = true;
        //System.out.println("File content>>>");
        while (next) {
            inLength = in.read(inBytes);
            if (inLength == blockSize) {
                int outLength = cipher.update(inBytes, 0, blockSize, outBytes);
                out.write(outBytes, 0, outLength);
                //System.out.print(new String(outBytes));
            } else {
                next = false;
            }
        }
        if (inLength > 0) {
            outBytes = cipher.doFinal(inBytes, 0, inLength);
        } else {
            outBytes = cipher.doFinal();
        }
        out.write(outBytes);
        //System.out.print(new String(outBytes));
        //System.out.println("<<<File content END");
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

}
