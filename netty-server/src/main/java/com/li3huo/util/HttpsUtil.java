/**
 * Create at Aug 29, 2013
 */
package com.li3huo.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author liyan
 * 
 */
public class HttpsUtil {
	/**
	 * 忽视证书HostName
	 */
	private static HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
		public boolean verify(String s, SSLSession sslsession) {
			System.out.println("WARNING: Hostname is not matched for cert.");
			return true;
		}
	};

	/**
	 * Ignore Certification
	 */
	private static TrustManager ignoreCertificationTrustManger = new X509TrustManager() {

		private X509Certificate[] certificates;

		public void checkClientTrusted(X509Certificate certificates[],
				String authType) throws CertificateException {
			if (this.certificates == null) {
				this.certificates = certificates;
				System.out.println("init at checkClientTrusted");
			}
		}

		public void checkServerTrusted(X509Certificate[] ax509certificate,
				String s) throws CertificateException {
			if (this.certificates == null) {
				this.certificates = ax509certificate;
				System.out.println("init at checkServerTrusted");
			}

			// for (int c = 0; c < certificates.length; c++) {
			// X509Certificate cert = certificates[c];
			// System.out.println(” Server certificate ” + (c + 1) + “:”);
			// System.out.println(“ Subject DN: ” + cert.getSubjectDN());
			// System.out.println(“ Signature Algorithm: “
			// + cert.getSigAlgName());
			// System.out.println(“ Valid from: ” + cert.getNotBefore());
			// System.out.println(“ Valid until: ” + cert.getNotAfter());
			// System.out.println(“ Issuer: ” + cert.getIssuerDN());
			// }

		}

		public X509Certificate[] getAcceptedIssuers() {
			// TODO Auto-generated method stub
			return null;
		}

	};

	public static byte[] doGet(String urlString) {

		ByteArrayOutputStream buffer = new ByteArrayOutputStream(512);
		try {

			URL url = new URL(urlString);

			/*
			 * use ignore host name verifier
			 */
			HttpsURLConnection
					.setDefaultHostnameVerifier(ignoreHostnameVerifier);
			HttpsURLConnection connection = (HttpsURLConnection) url
					.openConnection();

			// Prepare SSL Context
			TrustManager[] tm = { ignoreCertificationTrustManger };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());

			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			connection.setSSLSocketFactory(ssf);

			InputStream reader = connection.getInputStream();
			byte[] bytes = new byte[512];
			int length = reader.read(bytes);

			do {
				buffer.write(bytes, 0, length);
				length = reader.read(bytes);
			} while (length > 0);

			// result.setResponseData(bytes);
//			System.out.println(buffer.toString());
			reader.close();

			connection.disconnect();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
		}
		return buffer.toByteArray();
	}

	public static void main(String[] args) {
		String urlString = "https://www.google.com/adsense/";
		String output = new String(HttpsUtil.doGet(urlString));
		System.out.println(output);
	}
}
