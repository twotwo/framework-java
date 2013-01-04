/**
 * 
 */
package li3huo.spring.data.dao;

import java.util.List;

import li3huo.spring.data.domain.Address;

/**
 * @author liyan
 *
 */
public interface AddressDao {
	List<Address> findAll();
}
