/**
 * JedisTest.java create at May 23, 2017 6:13:12 PM
 */
package com.li3huo.jedis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @ClassName: JedisTest
 * @Description: 
 * @author liyan
 * @date May 23, 2017 6:13:12 PM
 *
 */
public class JedisTest {
	static final Logger logger = LogManager.getLogger(JedisTest.class.getName());

	static JedisPool redisPool;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/** 创建连接池实例 */
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		// 连接Redis的最大连接数，为负值时没有限制
		poolConfig.setMaxTotal(100);
		poolConfig.setMaxIdle(10);
		// 等待连接的最长时间，单位毫秒
		poolConfig.setMaxWaitMillis(1000);
		// 使用连接时，检测连接是否成功
		poolConfig.setTestOnBorrow(true);
		// 返回连接时，检测连接是否成功
		poolConfig.setTestOnReturn(true);

		String host = "172.16.100.60";
		int port = 6379;
		int timeout = 1000;
		String password = null;
		int database = 0;

		redisPool = new JedisPool(poolConfig, host, port, timeout, password, database);
	}

	@Test
	public void testAdd() {
		Jedis jedis = redisPool.getResource();
		String key = JedisTest.class.getName();
		if (jedis.exists(key)) {
			logger.info("testAdd() del {} = {}, response = {}", key, jedis.get(key), jedis.del(key));
		}
		String value = "test";
		logger.info("testAdd() response = {}, redisPool.getNumActive() = {}", jedis.set(key, value),
				redisPool.getNumActive());
		Assert.assertEquals(value, jedis.get(key));
		jedis.close();
		logger.info("testAdd() after close, redisPool.getNumActive() = {}", redisPool.getNumActive());
	}

	@Test
	public void testQuery() {
		Jedis jedis = redisPool.getResource();
		for (String key : jedis.keys("*")) {
			logger.info("testQuery() {} = {}", key, jedis.get(key));
		}

		jedis.close();
		logger.info("testQuery() after close, redisPool.getNumActive() = {}", redisPool.getNumActive());
	}

	@Test
	public void testExpire() {
		Jedis jedis = redisPool.getResource();
		int seconds = 1;
		String key = JedisTest.class.getName();
		jedis.setex(key, seconds, "testExpire()");
		Assert.assertNotNull(jedis.get(key));
		try {
			Thread.sleep(1100);
			Assert.assertNull(jedis.get(key));
		} catch (InterruptedException e) {

		} finally {
			jedis.close();
			logger.info("testExpire() after close, redisPool.getNumActive() = {}", redisPool.getNumActive());
		}
	}

}
