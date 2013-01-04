/**
 * 
 */
package li3huo.spring.data;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * @author liyan
 *
 */
@Configuration
public class TestConfig extends ApplicationConfig {

	/*
	 * Use Buildin HSQLDB for Test
	 * @see li3huo.spring.data.jdbc.ApplicationConfig#dataSource()
	 */
	@Bean
	@Override
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).addScript("classpath:sql/schema.sql")
				.addScript("classpath:sql/test-data.sql").build();
	}
}