/**
 * Create at Jan 31, 2013
 */
package com.li3huo.netty.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.alibaba.fastjson.JSON;

/**
 * @author liyan
 * 
 */
public class JSONTool {
	public static String toJSONString(Object object) {
		return JSON.toJSONString(object);
	}

	public static <T> T parseString2Object(String text, Class<T> clazz) {
		return JSON.parseObject(text, clazz);
	}

	public static <T> T parseFile2Object(String file, Class<T> clazz)
			throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(file))));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();
			reader = null;
		} catch (IOException ex) {
			throw ex;
		}finally {
			if(null != reader) {
				reader.close();
			}
		}

		return JSON.parseObject(sb.toString(), clazz);
	}
}
