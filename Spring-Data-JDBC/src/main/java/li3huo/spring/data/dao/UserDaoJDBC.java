/**
 * 
 */
package li3huo.spring.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import li3huo.spring.data.domain.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * @author liyan
 * 
 *         with mysql
 */
@Repository
public class UserDaoJDBC implements UserDao {
	private static int count = 0;
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public UserDaoJDBC(DataSource datasource) {
		this.jdbcTemplate = new JdbcTemplate(datasource);
		synchronized (this) {
			System.out.println(++count);
		}
	}

	/**
	 * 
	 * mysql> desc t_user;
	 * +------------------+-------------+------+-----+--------
	 * -+----------------+ | Field | Type | Null | Key | Default | Extra |
	 * +------
	 * ------------+-------------+------+-----+---------+----------------+ |
	 * user_id | bigint(20) | NO | PRI | NULL | auto_increment | | imsi |
	 * varchar(20) | YES | | NULL | | | imei | varchar(20) | YES | | NULL | | |
	 * phone_name | varchar(50) | YES | | NULL | | | phone_os | varchar(20) |
	 * YES | | NULL | | | phone_resolution | varchar(20) | YES | | NULL | | |
	 * version | bigint(10) | YES | | NULL | | | channelid | varchar(10) | YES |
	 * | NULL | | | createdate | datetime | NO | | NULL | | | pnid | varchar(50)
	 * | YES | | NULL | | | pntype | varchar(20) | YES | | NULL | |
	 * +------------
	 * ------+-------------+------+-----+---------+----------------+
	 * 
	 * 
	 * 
	 * @return
	 */
	RowMapper<User> userMapper() {
		return new RowMapper<User>() {

			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();
				user.setUserId(rs.getInt("user_id"));
				user.setImei(rs.getString("imei"));
				user.setImsi(rs.getString("imsi"));
				user.setPhoneName(rs.getString("phone_name"));
				return user;
			};
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see li3huo.spring.data.dao.UserDao#getUserCount()
	 */
	@Override
	public int getUserCount() {
		return this.jdbcTemplate.queryForInt("select count(*) from t_user");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see li3huo.spring.data.dao.UserDao#getUserbyID(int)
	 */
	@Override
	public User getUserbyID(int id) {
		return this.jdbcTemplate
				.queryForObject(
						"select user_id, imei, imsi, phone_name from t_user where user_id =?",
						new Object[] { id }, userMapper());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see li3huo.spring.data.dao.UserDao#getUsers()
	 */
	@Override
	public List<User> getUsers() {
		return this.jdbcTemplate.query(
				"select user_id, imei, imsi, phone_name from t_user",
				userMapper());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * li3huo.spring.data.dao.UserDao#addUser(li3huo.spring.data.domain.User)
	 */
	@Override
	public User addUser(final User user) {
		// create date set to now(), only work for mysql
		final String INSERT_SQL = "insert into  t_user (imei, imsi, phone_name,createdate)  values (?, ?, ?,  now())";

//		return this.jdbcTemplate.update(INSERT_SQL, user.getImei(),
//				user.getImsi(), user.getPhoneName());

		/*
		 * refer to Spring Framework Reference Documentation: 14.2.8 Retrieving
		 * auto-generated keys
		 */
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				//the String Array means columns that should be returned from the inserted rows
				PreparedStatement ps = connection.prepareStatement(INSERT_SQL,
						new String[] { "user_id" });
				ps.setString(1, user.getImei());
				ps.setString(2, user.getImsi());
				ps.setString(3, user.getPhoneName());
				
				return ps;
			}
		}, keyHolder);

		// keyHolder.getKey() now contains the generated key
		user.setUserId(keyHolder.getKey().intValue());
		return user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * li3huo.spring.data.dao.UserDao#updateUser(li3huo.spring.data.domain.User)
	 */
	@Override
	public int updateUser(User user) {
		// update t_user set imei="36722222221231", imsi="46022222221231",
		// phone_name="htc g7" where user_id=1
		return this.jdbcTemplate
				.update("update t_user set imei= ?, imsi=?, phone_name=?  where user_id=?",
						user.getImei(), user.getImsi(), user.getPhoneName(),
						user.getUserId());
	}

}