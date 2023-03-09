package ejb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.LogSeverityEnum;
import model.LogTypeEnum;
import model.Massage;
import model.Registration;
import util.CustomLogger;
import util.MyMassageProperties;

/**
 * Session Bean implementation class RegistrationEJB
 */
@Stateless
@LocalBean
@SuppressWarnings("unchecked")
public class RegistrationEJB implements RegistrationEJBRemote {
	@PersistenceContext(unitName = "mymassage-ejb")
	private EntityManager em;
    /**
     * Default constructor. 
     */
    public RegistrationEJB() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public Registration getRegistrationByMassage(Massage massage) {
		try {
			 List<Registration> regList = em.createNamedQuery("Registration.findByMassage").setParameter("id", massage.getId()).getResultList();
			 if(regList.isEmpty()) {
				 return null;
			 } else {
				 return regList.get(0);
			 }
		} catch (Exception e) {
			CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, MyMassageProperties.PARAM_SYSTEM_USER);
			return null;
		}
	}

	@Override
	public List<Registration> getAllRegistration() {
		try {
			return em.createNamedQuery("Registration.findAll").getResultList();
		} catch (Exception e) {
			CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, MyMassageProperties.PARAM_SYSTEM_USER);
			return null;
		}
	}

	@Override
	public Registration saveRegistration(Registration registration) {
		if (registration.getId() == null || registration.getId() == 0) {
			em.persist(registration);
		} else {
			em.merge(registration);
		}
		em.flush();
		// récupération du user pour mettre les id à jour
		registration = this.getRegistrationByMassage(registration.getMassage());
		return registration;
	}

	@Override
	public Registration findByUser(String lastName, String firstName) {
		try {
			List<Registration> newRegList = new ArrayList<Registration>();
			List<Registration> regList = em.createNamedQuery("Registration.findByUser").setParameter("firstName", firstName)
					.setParameter("lastName", lastName).getResultList();/*.setParameter("today", new Date(), TemporalType.DATE).getResultList();*/
			for (Registration r : regList) {
				if (r.getMassage().getDate().getTime() >= new Date().getTime()) {
					newRegList.add(r);
				}
			}
			if(newRegList.isEmpty()) {
				return null;
			} else {
				return newRegList.get(0);
			}
		} catch (Exception e) {
			if(!e.toString().contains("did not retrieve any entities")) {
				CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, MyMassageProperties.PARAM_SYSTEM_USER);
			}
			return null;
		}
	}

	@Override
	public List<Registration> findByReminder(Date date1, Date date2) {
		try {
			return em.createNamedQuery("Registration.findByReminder").setParameter("date1", date1)
					.setParameter("date2", date2).getResultList();
		} catch (Exception e) {
			CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, MyMassageProperties.PARAM_SYSTEM_USER);
			return null;
		}
	} 
}
