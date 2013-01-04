/**
 * 
 */
package li3huo.spring.data;

import static org.junit.Assert.assertNotSame;
import li3huo.spring.data.dao.UserDao;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author liyan
 *
 */
public class AppConfigTest {
	
	private Logger logger = Logger.getLogger(AppConfigTest.class);
	
	private ApplicationContext ctx = new AnnotationConfigApplicationContext(
			ApplicationConfig.class);

	/**
	 * Test method for {@link li3huo.spring.data.dao.UserDaoJDBC#getUserCount()}.
	 */
	@Test
	public void testGetUserCount() {

		UserDao userDao = ctx.getBean(UserDao.class);
		
		logger.debug(userDao.getUserCount());
		
		assertNotSame(0, userDao.getUserCount());
	}
	


}
