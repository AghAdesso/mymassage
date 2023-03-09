package ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.LogSeverityEnum;
import model.LogTypeEnum;
import model.Parameter;
import util.CustomLogger;
import util.MyMassageProperties;

/**
 * Session Bean implementation class ParameterEJB
 */
@Stateless
@LocalBean
@SuppressWarnings("unchecked")
public class ParameterEJB implements ParameterEJBRemote {
	@PersistenceContext(unitName = "mymassage-ejb")
	private EntityManager em;
	
    /**
     * Default constructor. 
     */
    public ParameterEJB() {
        // TODO Auto-generated constructor stub
    }
	
	@Override
	public List<Parameter> getGESParameter() {
		try {
			return em.createNamedQuery("Parameter.findByType").setParameter("type", MyMassageProperties.PARAMETER_TYPE_GES).getResultList();
		} catch (Exception e) {
			CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, MyMassageProperties.PARAM_SYSTEM_USER);
			return null;
		}
	}

	@Override
	public List<Parameter> getAllParameters() {
		try {
			return em.createNamedQuery("Parameter.findAll").getResultList();
		} catch (Exception e) {
			CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, MyMassageProperties.PARAM_SYSTEM_USER);
			return null;
		}
	}

	@Override
	public Parameter saveParameter(Parameter parameter) {
		em.merge(parameter);
		em.flush();
		
		return parameter;
		
	}

	@Override
	public Parameter getParameterByKey(String key) {
		try {
			return (Parameter) em.createNamedQuery("Parameter.findByKey").setParameter("key", key).getSingleResult();
		} catch (Exception e) {
			CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, MyMassageProperties.PARAM_SYSTEM_USER);
			return null;
		}
	}

}
