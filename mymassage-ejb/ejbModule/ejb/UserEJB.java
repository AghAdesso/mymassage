package ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.LogSeverityEnum;
import model.LogTypeEnum;
import model.User;
import util.CustomLogger;

/**
 * Session Bean implementation class UserEJB
 */
@Stateless
@LocalBean
@SuppressWarnings("unchecked")
public class UserEJB implements UserEJBRemote {
	@PersistenceContext(unitName = "mymassage-ejb")
	private EntityManager em;
    /**
     * Default constructor. 
     */
    public UserEJB() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
	public User getUserByLogin(String login) {
		try {
			return (User) em.createNamedQuery("User.findByLogin").setParameter("login", login).getSingleResult();
		} catch (Exception e) {
			//CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.INFO, LogTypeEnum.SYSTEM, "login : " + login + " pas trouvé en base de données", null);
			return null;
		}
	}
    
    @Override
	public User getUserById(int id) {
		try {
			return (User) em.createNamedQuery("User.findById").setParameter("id", id).getSingleResult();
		} catch (Exception e) {
			CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, null);
			return null;
		}
	}
    
    @Override
	public User saveUser(User user) {
		if (user.getId() == null || user.getId() == 0) {
			em.persist(user);
		} else {
			em.merge(user);
		}
		em.flush();
		// récupération du user pour mettre les id à jour
		user = this.getUserByLogin(user.getLogin());
		return user;
	}

	@Override
	public List<User> getAllUsers() {
		try {
			return em.createNamedQuery("User.findAll").getResultList();
		} catch (Exception e) {
			CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, null);
			return null;
		}
	}
    
    

}
