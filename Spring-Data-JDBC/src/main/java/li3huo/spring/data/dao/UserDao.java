package li3huo.spring.data.dao;

import java.util.List;

import li3huo.spring.data.domain.User;

public interface UserDao {

	public int getUserCount();

	public User getUserbyID(int id);

	public List<User> getUsers();

	public User addUser(User user);

	public int updateUser(User user);

}