/**
 * 
 */
package li3huo.spring.data.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import li3huo.spring.data.domain.Address;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * @author liyan
 *
 */
@Repository
public class AddressDaoJDBC implements AddressDao {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public AddressDaoJDBC(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/* (non-Javadoc)
	 * @see li3huo.spring.data.jdbc.domain.dao.AddressDao#findAll()
	 */
	@Override
	public List<Address> findAll() {
		//customer_id,?
		return this.jdbcTemplate.query(
				"select id,  street, city, country from address", addressMapper());
	}
	
	/**
	 * mapping row to object
	 * @return
	 */
	RowMapper<Address> addressMapper() {
		return new RowMapper<Address>() {

			public Address mapRow(ResultSet rs, int rowNum) throws SQLException {
				Address address = new Address();
				address.setId(rs.getLong("id"));
				address.setStreet(rs.getString("street"));
				address.setCity(rs.getString("city"));
				address.setCountry(rs.getString("country"));
				return address;
			};
		};
	}
}
