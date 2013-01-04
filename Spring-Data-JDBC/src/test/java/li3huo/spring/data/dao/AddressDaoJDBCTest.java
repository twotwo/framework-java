/**
 * 
 */
package li3huo.spring.data.dao;

import static org.junit.Assert.assertNotSame;

import java.util.List;

import javax.sql.DataSource;

import li3huo.spring.data.TestConfig;
import li3huo.spring.data.domain.Address;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author liyan
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@DirtiesContext
public class AddressDaoJDBCTest {
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	AddressDao addressDao;

	@Test
	public void testFindAll() {
		List<Address> results = addressDao.findAll();
		assertNotSame(0, results.size());
		for(Address address : results) {
			System.out.println(address.getCity());
		}
	}
	
	

}
