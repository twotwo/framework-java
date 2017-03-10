/**
 * 
 */
package com.li3huo.sdk.tools;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author liyan
 *
 */
public class RSAUtil {
	static final Logger logger = LogManager.getLogger(RSAUtil.class.getName());

	/**
	 * Generate Key Pair as keyFilePath & keyFilePath.pub
	 * 
	 * @param keyFilePath
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static void generateKeyPair(String keyFilePath) throws IOException, NoSuchAlgorithmException {

		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		// 密钥位数
		keyPairGen.initialize(4096);
		// Generate Key Pair
		KeyPair keyPair = keyPairGen.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		// encode to base64 string
		String priKeyStr = Base64.encodeBase64String(privateKey.getEncoded());
		String pubKeyStr = Base64.encodeBase64String(publicKey.getEncoded());
		logger.debug("generateKeyPair(): priKeyStr\n" + priKeyStr);
		logger.debug("generateKeyPair(): pubKeyStr\n" + pubKeyStr);
//		// Base64 encode first and write to file
//		FileUtils.writeByteArrayToFile(new File(keyFilePath), Base64.encodeBase64(privateKey.getEncoded()));
//		FileUtils.writeByteArrayToFile(new File(keyFilePath + ".pub"), Base64.encodeBase64(publicKey.getEncoded()));
		//Write with ----
		FileUtils.write(new File(keyFilePath), "-----BEGIN PRIVATE KEY-----\n"+priKeyStr+"\n-----END PRIVATE KEY-----","ISO8859-1");
		FileUtils.write(new File(keyFilePath + ".pub"), "-----BEGIN PUBLIC KEY-----\n"+pubKeyStr+"\n-----END PUBLIC KEY-----","ISO8859-1");
	}

	/**
	 * 从文件中加载私钥对象
	 * 
	 * @param keyFilePath
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static RSAPrivateKey loadPrivateKey(String keyFilePath)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		// read key from file
		String s = FileUtils.readFileToString(new File(keyFilePath), "ISO-8859-1");
		s = StringUtils.substringBetween(s, " KEY-----\n", "\n-----END ");
		logger.debug("loadPrivateKey:\n" + s);
		KeyFactory kf = KeyFactory.getInstance("RSA");

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(s));
		RSAPrivateKey key = (RSAPrivateKey) kf.generatePrivate(keySpec);

		return key;
	}

	public static RSAPublicKey loadPublicKey(String keyFilePath)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		// read key from file
		String s = FileUtils.readFileToString(new File(keyFilePath), "ISO-8859-1");
		s = StringUtils.substringBetween(s, " KEY-----\n", "\n-----END ");

		KeyFactory kf = KeyFactory.getInstance("RSA");
		logger.debug("loadPublicKey:\n" + s);

		// PKCS8EncodedKeySpec keySpec = new
		// PKCS8EncodedKeySpec(Base64.decodeBase64(s));
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(s));
		RSAPublicKey key = (RSAPublicKey) kf.generatePublic(keySpec);
		return key;
	}

	/**
	 * 
	 * @param txt
	 *            原文
	 * @param key
	 *            密钥(公钥或私钥)
	 * @return 密文
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static byte[] enc(String txt, Key key) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		// long start = System.currentTimeMillis();
		byte[] bytes = cipher.doFinal(txt.getBytes());
		// System.out.println("time:" + (System.currentTimeMillis() - start));
		return bytes;
	}

	/**
	 * 
	 * @param data:
	 *            encoded content
	 * @param key:
	 *            pub/pri key
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static byte[] dec(byte[] data, Key key) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);
		// long start = System.currentTimeMillis();
		byte[] bytes = cipher.doFinal(data);
		// System.out.println("time:" + (System.currentTimeMillis() - start));
		return bytes;
	}

}
