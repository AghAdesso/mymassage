package ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.LogSeverityEnum;
import model.LogTypeEnum;
import model.Role;
import util.CustomLogger;

/**
 * Session Bean implementation class RoleEJB
 */
@Stateless
@LocalBean
@SuppressWarnings("unchecked")
public class RoleEJB implements RoleEJBRemote {
	@PersistenceContext(unitName = "mymassage-ejb")
	private EntityManager em;
    /**
     * Default constructor. 
     */
    public RoleEJB() {
        // TODO Auto-generated constructor stub
    }
    @Override
    public Role getRoleByCode(String code) {
    	try {
			return (Role) em.createNamedQuery("Role.findByCode").setParameter("code", code).getSingleResult();
		} catch (Exception e) {
			CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, null);
			return null;
		}
    }

	@Override
	public List<Role> getAllRoles() {
		try {
			return em.createNamedQuery("Role.findAll").getResultList();
		} catch (Exception e) {
			CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, null);
			return null;
		}
	}


}
