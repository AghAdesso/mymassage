package ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.Log;
import model.LogSeverityEnum;
import model.LogTypeEnum;
import util.CustomLogger;
import util.MyMassageProperties;

/**
 * Session Bean implementation class LogEJB
 */
@Stateless
@LocalBean
@SuppressWarnings("unchecked")
public class LogEJB implements LogEJBRemote {
	@PersistenceContext(unitName = "mymassage-ejb")
	private EntityManager em;
    /**
     * Default constructor. 
     */
    public LogEJB() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public List<Log> getAllLogsByDate() {
		try {
			return em.createNamedQuery("Log.findAllByDate").setMaxResults(700).getResultList();
		} catch (Exception e) {
			CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, MyMassageProperties.PARAM_SYSTEM_USER);
			return null;
		}
	}

}
