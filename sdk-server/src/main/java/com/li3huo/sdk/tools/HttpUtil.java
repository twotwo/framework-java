package com.li3huo.sdk.tools;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author liyan
 *
 */
public class HttpUtil {

	private static Logger logger = LogManager.getLogger(HttpUtil.class.getName());

	private static int ConnectTimeoutMillis = 15000;
	// waiting for 30s as client
	private static int ReadTimeoutMillis = 30000;

	public static String getParameterString(Map<String, String> keyMap) throws UnsupportedEncodingException {
		// 制作请求url
		if (keyMap == null || keyMap.size() == 0) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for (String key : keyMap.keySet()) {
			sb.append(key).append("=");
			sb.append(URLEncoder.encode(keyMap.get(key), "utf-8")).append("&");
		}

		return StringUtils.removeEnd(sb.toString(), "&");
	}

	public static String doGet(String sURL, Map<String, String> headers) throws IOException {
		return doGet(sURL, null, headers);
	}

	/**
	 * 
	 * @param sURL
	 * @param data
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	public static String doGet(String sURL, String data, Map<String, String> headers) throws IOException {
		StringBuilder result = new StringBuilder();
		URL url = null;
		InputStreamReader in = null;

		HttpURLConnection urlConn;
		url = new URL(sURL);
		debug("prepare to connect ... " + sURL);
		urlConn = (HttpURLConnection) url.openConnection();

		try {

			// Set Headers
			if (null != headers) {
				for (String key : headers.keySet())
					urlConn.setRequestProperty(key, headers.get(key));
			}
			urlConn.setDoInput(true);
			if (null != data) {
				urlConn.setDoOutput(true);
				PrintWriter writer = new PrintWriter(urlConn.getOutputStream());
				writer.print(data);
				writer.flush();
				writer.close();
			}

			in = new InputStreamReader(urlConn.getInputStream(), "utf-8");

		} catch (Exception e) {
			in = new InputStreamReader(urlConn.getErrorStream(), "UTF-8");
		} finally {
			if (in != null) {
				BufferedReader buffer = new BufferedReader(in);
				String str = null;
				while ((str = buffer.readLine()) != null) {
					result.append(str);
				}
				in.close();
			}
		}
		return result.toString();
	}

	/**
	 * Format Parameters in HTTP POST
	 * 
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		}

		return result.toString();
	}

	public static String doPost(String sURL) throws IOException {

		URL url = new URL(sURL);
		StopWatch sw = new StopWatch();
		sw.start();
		debug("prepare to connect ... " + sURL);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		debug("connected ... " + sw);
		connection.setReadTimeout(ReadTimeoutMillis); // waiting for 30s as
														// client
		connection.setConnectTimeout(ConnectTimeoutMillis);
		connection.setRequestMethod("POST");

		StringBuffer buf = new StringBuffer();
		debug("prepare to read ... " + sw);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				// debug("reading line by line... "+sw);
				buf.append(line);
			}

		} catch (java.io.IOException ex) {
			logger.fatal(ex.getMessage() + "@" + sURL);
			throw ex;
		} finally {
			try {
				connection.disconnect();
			} catch (Exception ex) {
			}
			sw.stop();
			debug("read over ... " + sw);
		}
		return buf.toString();
	}

	public static String doPost(String sURL, String data) throws IOException {
		return doPost(sURL, data, null);
	}

	public static String doPost(String sURL, String data, Map<String, String> headers) throws IOException {

		URL url = new URL(sURL);
		StopWatch sw = new StopWatch();
		sw.start();
		debug("prepare to connect ... " + sURL);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		debug("connected ... " + sw);

		// Set Headers
		if (null != headers) {
			for (String key : headers.keySet())
				connection.setRequestProperty(key, headers.get(key));
		}

		connection.setReadTimeout(ReadTimeoutMillis); // waiting for 30s as
														// client
		connection.setConnectTimeout(ConnectTimeoutMillis);
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);

		debug("prepare to post ... " + sw);
		OutputStream raw = connection.getOutputStream();
		debug("post - getOutputStream ... ");
		OutputStream output = new BufferedOutputStream(raw);
		OutputStreamWriter out = new OutputStreamWriter(output, "UTF-8");
		debug("post - new OutputStreamWriter ... ");
		debug("post - ecrypt message ... ");
		out.write(data);
		debug("post - write message ... " + sw);
		out.flush();
		out.close();
		debug("post - write message over ... " + sw);

		StringBuffer buf = new StringBuffer();
		debug("prepare to read ... " + sw);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				// debug("reading line by line... "+sw);
				buf.append(line);
			}

		} catch (java.io.IOException ex) {
			logger.fatal(ex.getMessage() + "@" + sURL + " Current read = " + buf.toString());
			throw ex;
		} finally {
			try {
				connection.disconnect();
			} catch (Exception ex) {
			}
			sw.stop();
			debug("read over ... " + sw);
		}
		return buf.toString();
	}

	public static byte[] doPost(String sURL, byte[] data) throws Exception {

		URL url = new URL(sURL);
		debug("prepare to connect ... ");

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		debug("connected ... ");
		// Content-Type
		// connection.setRequestProperty("Content-Type",
		// "application/x-www-form-urlencoded; charset=UTF-8");
		connection.setReadTimeout(ReadTimeoutMillis); // waiting for 30s as
														// client
		connection.setConnectTimeout(ConnectTimeoutMillis);
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);

		debug("prepare to post ... ");

		DataOutputStream out = new DataOutputStream(connection.getOutputStream());
		debug("post - getOutputStream ... ");
		out.write(data);
		debug("post - write message ... ");
		out.flush();
		out.close();
		debug("post over ... ");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		debug("prepare to read ... ");
		try {
			InputStream in = connection.getInputStream();

			int b;
			while ((b = in.read()) != -1) {
				outputStream.write(b);
			}

		} catch (java.io.IOException ex) {
			logger.fatal(ex.getMessage() + "@" + sURL);
			throw ex;
		} finally {
			try {
				connection.disconnect();
			} catch (Exception ex) {
			}
		}
		debug("read over ... ");
		return outputStream.toByteArray();
	}

	private static void debug(String msg) {
		logger.debug(msg);
	}

	public static String doHttpsPost(String sURL, String data) throws IOException {
		return httpSPost(sURL, data);
	}

	private static String httpSPost(String httpsUrl, String data) {
		HttpsURLConnection connection = null;
		String result = "";
		InputStream in = null;
		OutputStream out = null;
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { myX509TrustManager };
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, tm, new SecureRandom());
			URL url = new URL(httpsUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setSSLSocketFactory(sslContext.getSocketFactory());
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.connect();
			out = connection.getOutputStream();
			byte[] reqeuestBytesData = data.getBytes("UTF-8");
			out.write(reqeuestBytesData);
			in = connection.getInputStream();
			result = IOUtils.toString(in, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			IOUtils.close(connection);
		}
		return result;
	}

	private static TrustManager myX509TrustManager = new X509TrustManager() {

		/**
		 * 该方法检查客户端的证书，若不信任该证书则抛出异常。 由于我们不需要对客户端进行认证，因此我们只需要执行默认的信任管理器的这个方法。
		 * JSSE中，默认的信任管理器类为TrustManager。
		 */
		@Override
		public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		/**
		 * 该方法检查服务器的证书，若不信任该证书同样抛出异常。通过自己实现该方法，
		 * 可以使之信任我们指定的任何证书。在实现该方法时，也可以简单的不做任何处理， 即一个空的函数体，由于不会抛出异常，它就会信任任何证书。
		 */
		@Override
		public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		/**
		 * 返回受信任的X509证书数组。
		 */
		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

	};

}
