/**
 * 
 */
package com.li3huo.sdk.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * @author liyan
 *
 */
public class JSONTest {
	static final Logger logger = LogManager.getLogger(JSONTest.class.getName());

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	public void parseObject() {
		String text = "{\"age\":18,\"phone\":[\"12345678\",\"87654321\"]}";
		JSONObject person = JSON.parseObject(text);
		logger.debug(person.toJSONString());
		Assert.assertEquals("18", person.getString("age"));
	}

	@Test
	public void toJSONString() {
		// 首先最外层是{}，是创建一个对象
		JSONObject person = new JSONObject();
		try {
			// 第一个键phone的值是数组，所以需要创建数组对象
			JSONArray phone = new JSONArray();
			phone.add("12345678");
			phone.add("87654321");
			person.put("phone", phone);

			person.put("name", "yuanzhifei89");
			person.put("age", 100);
			// 键address的值是对象，所以又要创建一个对象
			JSONObject address = new JSONObject();
			address.put("country", "china");
			address.put("province", "jiangsu");
			person.put("address", address);
			person.put("married", false);
		} catch (JSONException ex) {
			// 键为null或使用json不支持的数字格式(NaN, infinities)
			throw new RuntimeException(ex);
		}
		logger.debug(person.toJSONString());
		Assert.assertEquals("china", person.getJSONObject("address").getString("country"));
	}

}
