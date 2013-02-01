package com.li3huo.netty.util;
/**
 * 
 */


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import org.apache.log4j.Logger;


/**
 * @author liyan
 *
 */
public class HttpTool {
	
	private static Logger log = Logger.getLogger("HttpClient");
	
	public static String doGetRequest(String sURL, Properties param) throws Exception {
		sURL += "?"; 
		for(String key:param.stringPropertyNames()) {
			sURL += key + "=";
			sURL += URLEncoder.encode(param.getProperty(key), "utf-8")+"&";
		}
		
		sURL = sURL.substring(0,sURL.length()-1);
		log.debug("url="+sURL);
		
		URL url = new URL(sURL);
		log.debug("prepare to connect ... ");
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		//connect to host
		connection.connect();
		StringBuffer buf = new StringBuffer();
		log.debug("prepare to read ... ");
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while((line = reader.readLine()) != null) {
				buf.append(line);
			}

		} catch (java.io.IOException ex) {
			log.fatal("Load response failed!");
			log.fatal(ex.getMessage());
		} finally {
			try {
				connection.disconnect();
			} catch (Exception ex) {
			}
		}
		log.debug("read over ... ");
		
		return buf.toString();
	}
	
	public static String doPostRequest(String sURL, String str)
			throws Exception {

		URL url = new URL(sURL);
		log.debug("prepare to connect ... ");
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		log.debug("connected ... " );
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);

		log.debug("prepare to post ... ");
		OutputStream raw = connection.getOutputStream();
		log.debug("post - getOutputStream ... ");
		OutputStream output = new BufferedOutputStream(raw);
		OutputStreamWriter out = new OutputStreamWriter(output, "UTF-8");
		log.debug("post - new OutputStreamWriter ... " );
		String sRequestStr = str;
		log.debug("post - ecrypt message ... ");
		out.write(sRequestStr);
		log.debug("post - write message ... ");
		out.flush();
		out.close();
		log.debug("post over ... ");

		StringBuffer buf = new StringBuffer();
		log.debug("prepare to read ... ");
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while((line = reader.readLine()) != null) {
				buf.append(line);
			}

		} catch (java.io.IOException ex) {
			log.fatal("Load response failed!");
			log.fatal(ex.getMessage());
		} finally {
			try {
				connection.disconnect();
			} catch (Exception ex) {
			}
		}
		log.debug("read over ... ");
		return buf.toString();
	}
	
	public static byte[] doPostRequest(String sURL, byte[] bytes)
			throws Exception {

		URL url = new URL(sURL);
		log.debug("prepare to connect ... ");
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		log.debug("connected ... " );
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);

		log.debug("prepare to post ... ");
		OutputStream out = connection.getOutputStream();
		log.debug("post - getOutputStream ... ");
		out.write(bytes);
		log.debug("post - write message ... ");
		out.flush();
		out.close();
		log.debug("post over ... ");

		ByteArrayOutputStream buf=new ByteArrayOutputStream();
		log.debug("prepare to read ... ");
		try {
			InputStream in = connection.getInputStream();
			int b;
			while((b = in.read()) != -1) {
				buf.write(b);
			}

		} catch (java.io.IOException ex) {
			log.fatal("Load response failed!");
			log.fatal(ex.getMessage());
		} finally {
			try {
				connection.disconnect();
			} catch (Exception ex) {
			}
		}
		log.debug("read over ... ");
		return buf.toByteArray();
	}
	

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String sURL="http://172.27.236.15:8080/test";
		String request="地方地方微服私访";
		String resp = HttpTool.doPostRequest(sURL, request);
		log.info("get response---");
		log.info(resp);
	}

}
