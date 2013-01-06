/**
 * 
 */
package com.li3huo.mybatis;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import com.li3huo.mybatis.domain.Product;
import com.li3huo.mybatis.domain.ProductMapper;

/**
 * @author liyan
 * 
 */
public class DaoTest {

	static SqlSessionFactory sqlSessionFactory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		inputStream.close();
	}

	@Test
	public void getSession() throws Exception {
		assertNotNull(sqlSessionFactory);

		SqlSession session = sqlSessionFactory.openSession();
		try {
			assertNotNull(session);
		} finally {
			session.close();
		}
	}

	@Test
	public void selectProduct() throws Exception {

		SqlSession session = sqlSessionFactory.openSession();
		try {
			ProductMapper mapper = session.getMapper(ProductMapper.class);
			Product product = mapper.selectProduct(1);
			assertNotNull(product);
			//INSERT INTO products (id, description, price) values(1, 'Lamp', 5.78);
			assertEquals("Lamp", product.getDescription());
			assertEquals(5.8, product.getPrice(), 0.1);
		} finally {
			session.close();
		}
	}
}
