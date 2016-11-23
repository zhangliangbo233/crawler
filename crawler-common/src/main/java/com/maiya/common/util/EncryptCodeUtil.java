package com.maiya.common.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

/**
 * des 加解密工具类
 */
public class EncryptCodeUtil {

    private final static String DES = "DES";

    /**
     * 加密
     *
     * @param src 數據源
     * @param key 密鑰，長度必須是8的倍數
     * @return 返回加密後的數據
     * @throws Exception
     */
    public static byte[] encrypt(byte[] src, byte[] key) throws RuntimeException {
        //      DES算法要求有一個可信任的隨機數源
        try {
            SecureRandom sr = new SecureRandom();
            // 從原始密匙數據創建DESKeySpec對象
            DESKeySpec dks = new DESKeySpec(key);
            // 創建一個密匙工廠，然後用它把DESKeySpec轉換成
            // 一個SecretKey對象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey securekey = keyFactory.generateSecret(dks);
            // Cipher對象實際完成加密操作
            Cipher cipher = Cipher.getInstance(DES);
            // 用密匙初始化Cipher對象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
            // 現在，獲取數據並加密
            // 正式執行加密操作
            return cipher.doFinal(src);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解密
     *
     * @param src 數據源
     * @param key 密鑰，長度必須是8的倍數
     * @return 返回解密後的原始數據
     * @throws Exception
     */
    public static byte[] decrypt(byte[] src, byte[] key) throws RuntimeException {
        try {
            //      DES算法要求有一個可信任的隨機數源
            SecureRandom sr = new SecureRandom();
            // 從原始密匙數據創建一個DESKeySpec對象
            DESKeySpec dks = new DESKeySpec(key);
            // 創建一個密匙工廠，然後用它把DESKeySpec對象轉換成
            // 一個SecretKey對象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey securekey = keyFactory.generateSecret(dks);
            // Cipher對象實際完成解密操作
            Cipher cipher = Cipher.getInstance(DES);
            // 用密匙初始化Cipher對象
            cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
            // 現在，獲取數據並解密
            // 正式執行解密操作
            return cipher.doFinal(src);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * DES數據解密
     *
     * @param data
     * @param key  密鑰
     * @return
     * @throws Exception
     */
    public final static String decrypt(String data, String key) {
        return new String(decrypt(hex2byte(data.getBytes()), key.getBytes()));
    }

    /**
     * DES數據加密
     * @param data
     * @param key  密鑰
     * @return
     * @throws Exception
     */
    public final static String encrypt(String data, String key) {
        if (data != null)
            try {
                return byte2hex(encrypt(data.getBytes(), key.getBytes()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        return null;
    }

    /**
     * 二行制轉字符串
     *
     * @param b
     * @return
     */
    private static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }

    private static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException();
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

}
