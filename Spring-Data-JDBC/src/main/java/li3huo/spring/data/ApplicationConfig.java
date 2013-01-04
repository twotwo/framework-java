/**
 * 
 */
package li3huo.spring.data;

import javax.sql.DataSource;

import li3huo.spring.data.dao.UserDao;
import li3huo.spring.data.dao.UserDaoJDBC;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * @author liyan
 * 
 */
@Configuration
@ComponentScan
@PropertySource("classpath:jdbc.properties")
public class ApplicationConfig {

	@Autowired
	Environment env;

	@Bean
	public DataSource dataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(env.getProperty("jdbc.driverClassName"));
		ds.setUrl(env.getProperty("jdbc.url"));
		ds.setUsername(env.getProperty("jdbc.username"));
		ds.setPassword(env.getProperty("jdbc.password"));
		return ds;
	}

	@Bean
	public UserDao userDao() {
		return new UserDaoJDBC(dataSource());
	}

//	@Bean
//	public AddressDao addressDao() {
//		return new AddressDaoJDBC(dataSource());
//	}
}
