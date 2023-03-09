package ejb;

import java.util.List;

import javax.ejb.Remote;

import model.User;

@Remote
public interface UserEJBRemote {
	User getUserByLogin(String login);
	User getUserById(int id);
	User saveUser(User user);
	List<User> getAllUsers();
}
