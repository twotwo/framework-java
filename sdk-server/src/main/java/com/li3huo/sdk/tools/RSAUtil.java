/**
 * 
 */
package com.li3huo.sdk.tools;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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
		int size = 1024;
		keyPairGen.initialize(size);
		// Generate Key Pair
		KeyPair keyPair = keyPairGen.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		// encode to base64 string
		String priKeyStr = Base64.encodeBase64String(privateKey.getEncoded());
		String pubKeyStr = Base64.encodeBase64String(publicKey.getEncoded());
		logger.debug("generateKeyPair(): priKeyStr\n" + priKeyStr);
		logger.debug("generateKeyPair(): pubKeyStr\n" + pubKeyStr);
		// // Base64 encode first and write to file
		// FileUtils.writeByteArrayToFile(new File(keyFilePath),
		// Base64.encodeBase64(privateKey.getEncoded()));
		// FileUtils.writeByteArrayToFile(new File(keyFilePath + ".pub"),
		// Base64.encodeBase64(publicKey.getEncoded()));
		// Write with ----
		FileUtils.write(new File(keyFilePath),
				"-----BEGIN PRIVATE KEY-----\n" + priKeyStr + "\n-----END PRIVATE KEY-----", "ISO8859-1");
		FileUtils.write(new File(keyFilePath + ".pub"),
				"-----BEGIN PUBLIC KEY-----\n" + pubKeyStr + "\n-----END PUBLIC KEY-----", "ISO8859-1");
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
		logger.debug("loadPrivateKey() key:\n" + s);
		if (StringUtils.contains(s, "KEY-----\n")) {
			s = StringUtils.substringBetween(s, " KEY-----\n", "\n-----END ");
		}
		return parsePrivateKey(s);
	}

	/**
	 * 
	 * @param keyStr
	 *            密钥字串(不含---- KEY -----)
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static RSAPrivateKey parsePrivateKey(String keyStr)
			throws NoSuchAlgorithmException, InvalidKeySpecException {

		logger.debug("parsePrivateKey() key:\n" + keyStr);
		KeyFactory kf = KeyFactory.getInstance("RSA");

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(keyStr));
		RSAPrivateKey key = (RSAPrivateKey) kf.generatePrivate(keySpec);

		return key;
	}

	public static RSAPublicKey loadPublicKey(String keyFilePath)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		// read key from file
		String s = FileUtils.readFileToString(new File(keyFilePath), "ISO-8859-1");
		logger.debug("loadPublicKey() key:\n" + s);
		if (StringUtils.contains(s, "KEY-----\n")) {
			s = StringUtils.substringBetween(s, " KEY-----\n", "\n-----END ");
		}

		return parsePublicKey(s);
	}

	/**
	 * 
	 * @param keyStr
	 *            公钥字串(不含---- KEY -----)
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static RSAPublicKey parsePublicKey(String keyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
		logger.debug("parsePublicKey() key:\n" + keyStr);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		// PKCS8EncodedKeySpec keySpec = new
		// PKCS8EncodedKeySpec(Base64.decodeBase64(s));
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(keyStr));
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

	public static String enc2str(String txt, Key key) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		return bytesToHexString(enc(txt, key)).toString();
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

	/**
	 * 转换成Hex(cp from HmacSHA1Encryption.java - xiaomi)
	 * 
	 * @param bytesArray
	 */
	public static StringBuilder bytesToHexString(byte[] bytesArray) {
		if (bytesArray == null) {
			return null;
		}
		StringBuilder sBuilder = new StringBuilder();
		for (byte b : bytesArray) {
			String hv = String.format("%02x", b);
			sBuilder.append(hv);
		}
		return sBuilder;
	}

	/**
	 * 使用 HMAC-SHA1 签名方法对对 encryptText 进行签名
	 * 
	 * (cp from HmacSHA1Encryption.java - xiaomi)
	 * 
	 * @param encryptData
	 *            被签名的字符串
	 * @param encryptKey
	 *            密钥
	 * @return 返回被加密后的字符串
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws Exception
	 */
	public static String HmacSHA1Encrypt(byte[] encryptData, String encryptKey)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
		String MAC_NAME = "HmacSHA1";
		byte[] data = encryptKey.getBytes("UTF-8");
		SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
		Mac mac = Mac.getInstance(MAC_NAME);
		mac.init(secretKey);
		byte[] digest = mac.doFinal(encryptData);
		StringBuilder sBuilder = bytesToHexString(digest);
		return sBuilder.toString();
	}

	/**
	 * 用私钥做SHA1签名(ref from com.lenovo.pay.sign.Rsa.sign() - lenovo)
	 * 
	 * @param content
	 * @param privateKey
	 * @param input_charset
	 * @return
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws UnsupportedEncodingException
	 * @throws SignatureException
	 */
	public static String signWithSHA1WithRSA(String content, String privateKey, String input_charset)
			throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException,
			UnsupportedEncodingException {
		// PKCS8EncodedKeySpec priPKCS8 = new
		// PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
		// KeyFactory keyf = KeyFactory.getInstance("RSA");
		// PrivateKey priKey = keyf.generatePrivate(priPKCS8);
		PrivateKey priKey = parsePrivateKey(privateKey);
		Signature signature = Signature.getInstance("SHA1WithRSA");
		signature.initSign(priKey);
		signature.update(content.getBytes(input_charset));
		byte signed[] = signature.sign();
		return Base64.encodeBase64String(signed);
	}

	/**
	 * 用私钥对内容做签名
	 * 
	 * @param content
	 * @param priKey
	 * @param dsa
	 *            数字签名算法 SHA1WithRSA,
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException
	 */
	public static String sign(byte[] content, PrivateKey priKey, String dsa) throws NoSuchAlgorithmException,
			InvalidKeySpecException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
		Signature signature = Signature.getInstance(dsa);
		signature.initSign(priKey);
		signature.update(content);
		byte signed[] = signature.sign();
		return Base64.encodeBase64String(signed);
	}

	/**
	 * 用公钥校验签名
	 * 
	 * @param data
	 * @param sign
	 * @param pubKey
	 * @param dsa(数字摘要算法): SHA256WithRSA/SHA1WithRSA/MD5withRSA
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException
	 */
	public static boolean verify(byte[] data, String sign, PublicKey pubKey, String dsa)
			throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException,
			UnsupportedEncodingException {
		Signature signature = Signature.getInstance(dsa);
		signature.initVerify(pubKey);
		signature.update(data);
		return signature.verify(Base64.decodeBase64(sign));
	}

}
