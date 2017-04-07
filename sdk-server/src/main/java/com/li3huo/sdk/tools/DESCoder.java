package com.li3huo.sdk.tools;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;


/**
 * DES安全编码组件
 */
public abstract class DESCoder{
	
    public static final String ALGORITHM = "DESede";

    /**
     * des3解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(String data, String key) throws Exception {
    	byte[] dataBytes = Base64.decodeBase64(data);
        SecretKey k = new SecretKeySpec(key.getBytes("UTF-8"), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k);
        return cipher.doFinal(dataBytes);
    }
    
    public static void main(String[] args) throws Exception {
    	String data = "bGtJ4lBUnKV7VFn0i9xHLZaIFiX5IgesDdgwQuJNnjg=";
    	String key = "YmJD4KvL0yQ99S3ToLb3txvJ";
    	byte[] decrypt = DESCoder.decrypt(data, key);
    	System.out.println(new String(decrypt, "utf-8"));
	}
}

