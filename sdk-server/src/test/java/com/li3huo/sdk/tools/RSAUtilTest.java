/**
 * 
 */
package com.li3huo.sdk.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author liyan
 *
 */
public class RSAUtilTest {

	static final Logger logger = LogManager.getLogger(RSAUtilTest.class.getName());

	static RSAPublicKey pubKey;
	static RSAPrivateKey priKey;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		StopWatch sw = new StopWatch();
		logger.info("Generate key first!");
		logger.error("ssh-keygen -t rsa -b 4096 -C \"admin@li3huo.com\" -f /tmp/key -q -N \"\"");
		// change ssh-rsa to pem type
		logger.error("ssh-keygen -e -m pem -f /tmp/key > /tmp/key.pub");

		try {
			sw.start();
			// create key pair if not exist
			if (!new File("/tmp/key").exists()) {
				logger.debug("create key pair...");
				RSAUtil.generateKeyPair("/tmp/key");
			}

			pubKey = RSAUtil.loadPublicKey("/tmp/key.pub");
			priKey = RSAUtil.loadPrivateKey("/tmp/key");
		} catch (NoSuchAlgorithmException e) {
			logger.fatal("setUpBeforeClass(): Failed to get RSA instance.", e);
		} catch (InvalidKeySpecException e) {
			logger.fatal("setUpBeforeClass(): Invalid Key Spec.", e);
		} catch (IOException e) {
			logger.fatal("setUpBeforeClass(): Failed to load file content.", e);
		} finally {
			logger.debug("setUpBeforeClass(): [" + sw + "]");
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void pubKeyEnc2priKeyDec() {
		String txt = "eat your own dog food!\n宁愿做过了后悔,也不要错过了后悔！";
		StopWatch sw = new StopWatch();

		try {
			sw.start();
			String ciphertxt = Base64.encodeBase64String(RSAUtil.enc(txt, pubKey));
			logger.debug("pubKeyEnc2priKeyDec(): [" + sw + "]ciphertxt = " + ciphertxt);

			byte[] bytes = RSAUtil.dec(Base64.decodeBase64(ciphertxt), priKey);
			logger.debug("pubKeyEnc2priKeyDec(): [" + sw + "]txt2 = "
					+ StringUtils.toEncodedString(bytes, Charset.forName("UTF-8")));

			Assert.assertEquals("should be equal. ", txt, StringUtils.toEncodedString(bytes, Charset.forName("UTF-8")));
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			logger.fatal("pubKeyEnc2priKeyDec()", e);
		} finally {
			logger.debug("pubKeyEnc2priKeyDec(): [" + sw + "]");
		}
	}

	@Test
	public void loadKeyFromResource() {
		logger.debug("loadKeyFromResource() key.pub = " + getClass().getResource("/test/key.pub").getPath());
		try {
			RSAPublicKey pubKey = RSAUtil.loadPublicKey(getClass().getResource("/test/key.pub").getPath());
			Assert.assertNotNull("can load pub key", pubKey);

		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			logger.fatal("loadKeyFromResource() " + e.getMessage(), e);
		}
	}

}
