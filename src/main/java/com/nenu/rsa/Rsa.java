package com.nenu.rsa;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 * The class Rsa.java
 */
public class Rsa {

    private static String PUBLIC_KEY_FILE = "C:\\sign\\PublicKey";
    private static String PRIVATE_KEY_FILE = "C:\\sign\\PrivateKey";

    /**
     * 初始化密钥
     *
     * @return
     */
    public static void productKey() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair keyPair = keyGen.generateKeyPair();// 生成密钥对
            Key pubKey = keyPair.getPublic(); // 获取公钥
            Key priKey = keyPair.getPrivate(); // 获取私钥
            ObjectOutputStream oos1 = null;
            ObjectOutputStream oos2 = null;
            try {
                /** 用对象流将生成的密钥写入文件 */
                oos1 = new ObjectOutputStream(new FileOutputStream(PUBLIC_KEY_FILE));
                oos2 = new ObjectOutputStream(new FileOutputStream(PRIVATE_KEY_FILE));
                oos1.writeObject(pubKey);
                oos2.writeObject(priKey);
            } catch (Exception e) {
                throw e;
            } finally {
                /** 清空缓存，关闭文件输出流 */
                oos1.close();
                oos2.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 公钥加密方法 私钥加密也一样
     *
     * @param source
     *            源数据
     * @return
     * @throws Exception
     */
    public static String encrypt(String source) throws Exception {
        Key publicKey;
        ObjectInputStream ois = null;
        try {
            /** 将文件中的公钥对象读出 */
            ois = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
            publicKey = (Key) ois.readObject();
        } catch (Exception e) {
            throw e;
        } finally {
            ois.close();
        }

        /** 得到Cipher对象来实现对源数据的RSA加密 */
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] b = source.getBytes();
        /** 执行加密操作 */
        byte[] b1 = cipher.doFinal(b);
        return Base64.encodeBase64String(b1);
    }

    /**
     * 私钥解密算法 公钥解密一样
     *
     * @param cryptograph
     *            密文
     * @return
     * @throws Exception
     */
    public static String decrypt(String cryptograph) throws Exception {
        Key privateKey;
        ObjectInputStream ois = null;
        try {
            /** 将文件中的私钥对象读出 */
            ois = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
            privateKey = (Key) ois.readObject();
        } catch (Exception e) {
            throw e;
        } finally {
            ois.close();
        }

        /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] b1 = Base64.decodeBase64(cryptograph);

        /** 执行解密操作 */
        byte[] b = cipher.doFinal(b1);
        return new String(b);
    }

    public static void main(String[] args) throws Exception {
        Rsa.productKey();
        String msg = "我是加密信息";
        String encryt = Rsa.encrypt(msg);
        System.out.println("加密后的字符:"+encryt);
        System.out.println("解密后的字符:"+Rsa.decrypt(encryt));
    }
}
