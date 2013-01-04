/**
 * 
 */
package li3huo.spring.data.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.List;

import javax.sql.DataSource;

import li3huo.spring.data.ApplicationConfig;
import li3huo.spring.data.domain.User;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author liyan
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
public class UserDaoJDBCTest {
	
	private Logger logger = Logger.getLogger(UserDaoJDBCTest.class);
	@Autowired
	DataSource dataSource;
	
	@Autowired
	UserDao userDao;

	/**
	 * Test method for {@link li3huo.spring.data.dao.UserDaoJDBC#getUserCount()}.
	 */
	@Test
	public void testGetUserCount() {
		logger.debug(userDao.getUserCount());
		assertNotSame(0, userDao.getUserCount());
	}

	/**
	 * Test method for {@link li3huo.spring.data.dao.UserDaoJDBC#getUserbyID(int)}.
	 */
	@Test
	public void testGetUserbyID() {
		
		User user = userDao.getUserbyID(1);
		assertNotSame(null, user);
		
		logger.debug("user is "+user);
	}

	/**
	 * Test method for {@link li3huo.spring.data.dao.UserDaoJDBC#getUsers()}.
	 */
	@Test
	public void testGetUsers() {
		List<User> users = userDao.getUsers();
		assertSame(users.size(), userDao.getUserCount());
	}

	/**
	 * Test method for {@link li3huo.spring.data.dao.UserDaoJDBC#addUser(li3huo.spring.data.domain.User)}.
	 */
	@Test
	public void testAddUser() {
		String newImei = "46022222222222";

		int count = userDao.getUserCount();
		logger.debug("user count is "+count);
		
		User user = new User();
		user.setImei(newImei);
		user.setImsi(newImei);
		user.setPhoneName("Shanzhai");
		logger.info("before add:"+user);
		
		user = userDao.addUser(user);
		//User ID should get from db
		assertNotSame(0, user.getUserId());
		logger.info("after add:"+user);
		
		user = userDao.getUserbyID(1);
		assertEquals(count+1, userDao.getUserCount());
	}

	/**
	 * Test method for {@link li3huo.spring.data.dao.UserDaoJDBC#updateUser(li3huo.spring.data.domain.User)}.
	 */
	@Test
	public void testUpdateUser() {
		
		User user = userDao.getUserbyID(1);
		assertNotSame(null, user);
		logger.debug("old user is "+user);
		String oldImei = user.getImei();
		String newImei = "46022222222222";
		assertNotSame(oldImei, newImei);
		
		user.setImei(newImei);
		// update 1 row
		logger.debug("change user is "+user);
		logger.debug("update row:"+ userDao.updateUser(user));
//		assertEquals(1,userDao.updateUser(user));
		
		user = userDao.getUserbyID(1);
		assertEquals(newImei, user.getImei());
		
		user.setImei(oldImei);
		userDao.updateUser(user);
		logger.debug("recover"+user);
	}

}
