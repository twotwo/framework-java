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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

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

	/**
	 * 
	 * @param sURL
	 * @return
	 */
	public static String doGet(String sURL) {
		StringBuilder result = new StringBuilder();
		URL url = null;
		try {
			url = new URL(sURL);
			debug("prepare to connect ... " + sURL);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(urlConn.getInputStream(), "utf-8");
			BufferedReader buffer = new BufferedReader(in);
			String str = null;
			while ((str = buffer.readLine()) != null) {
				result.append(str);
			}
			in.close();
			urlConn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
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

	public static byte[] doPost(String sURL, byte[] data) throws Exception {

		URL url = new URL(sURL);
		debug("prepare to connect ... ");

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		debug("connected ... ");
		// Content-Type
//		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
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

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String sURL = "http://172.27.236.15:8080/test";
		String request = "地方地方微服私访";
		String resp = HttpUtil.doPost(sURL, request);
		logger.info("get response---");
		logger.info(resp);
	}

}
