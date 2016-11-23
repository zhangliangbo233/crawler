package com.maiya.common.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加解密
 * 
 * @author ck
 *
 */
public class EncrypAES {

	// 算法名称
	public static final String KEY_ALGORITHM = "AES";
	// 算法名称/加密模式/填充方式
	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

	/**
	 * 对字符串加密，返回一个list，第一个元素是加密后的字节数组，第二个元素是密钥
	 * 
	 * @param str
	 * @return
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static List<byte[]> encrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException {
		SecretKey key = generatePrivateKey();
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);

		List<byte[]> result = new ArrayList<>();
		result.add(cipher.doFinal(data));
		result.add(key.getEncoded());
		return result;
	}

	/**
	 * 对字符串解密
	 * 
	 * @param data
	 *            加密后的字节数组
	 * @param privateKey
	 *            密钥
	 * @return
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	public static byte[] decrypt(byte[] data, byte[] privateKey) throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		Key k = toKey(privateKey);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, k);
		return cipher.doFinal(data);
	}

	private static SecretKey generatePrivateKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = null;
		keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
		SecretKey secretKey = keyGenerator.generateKey();
		return secretKey;
	}

	private static Key toKey(byte[] key) {
		SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
		return secretKey;
	}

	/**
	 * @param args
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidKeyException
	 */
	public static void main(String[] args) throws Exception {
		String msg = "xdfjd198781xxxxf";
		List<byte[]> encontent = EncrypAES.encrypt(msg.getBytes());
		System.out.println("密钥长度:" + encontent.get(1).length);
		System.out.println("密钥:"+new String(encontent.get(1)));
		byte[] decontent = EncrypAES.decrypt(encontent.get(0), encontent.get(1));
		System.out.println("明文是:" + msg);
		System.out.println("加密后:" + new String(encontent.get(0)));
		System.out.println("解密后:" + new String(decontent));
	}

}
