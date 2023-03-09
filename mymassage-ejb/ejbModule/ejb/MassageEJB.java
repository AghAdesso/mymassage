package ejb;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.LogSeverityEnum;
import model.LogTypeEnum;
import model.Massage;
import model.Registration;
import util.CustomLogger;
import util.LoroDate;
import util.MyMassageProperties;

/**
 * Session Bean implementation class MassageEJB
 */
@Stateless
@LocalBean
@SuppressWarnings("unchecked")
public class MassageEJB implements MassageEJBRemote {
	@PersistenceContext(unitName = "mymassage-ejb")
	private EntityManager em;
	
	@EJB
	private RegistrationEJBRemote reg;
	
    /**
     * Default constructor. 
     */
    public MassageEJB() {
        // TODO Auto-generated constructor stub
    }
    
    private void SetUserRegistered(List<Massage> list)
    {
    	for (Massage m : list)
    	{
    		Registration r = null;
    		try 
    		{
    			r = reg.getRegistrationByMassage(m);
    			m.setUserRegistred(r.getFirstName() + " " + r.getLastName());
    		}
    		catch (Exception e)
    		{
    			m.setUserRegistred("-");
    		}
    	}
    }
    
    private void SetUserRegistered(Massage m)
    {
    	Registration r = null;
		try 
		{
			r = reg.getRegistrationByMassage(m);
			m.setUserRegistred(r.getFirstName() + " " + r.getLastName());
		}
		catch (Exception e)
		{
			m.setUserRegistred("-");
		}
    }

	
	@Override
	public List<Massage> getAllMassage() {
		try {
			List<Massage> list = em.createNamedQuery("Massage.findAll").getResultList();
			this.SetUserRegistered(list);
			return list;
		} catch (Exception e) {
			CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, MyMassageProperties.PARAM_SYSTEM_USER);
			return null;
		}
	}


	@Override
	public List<Massage> getMessageBetweenDates(Date date1, Date date2) {
		try {
			List<Massage> list = em.createNamedQuery("Massage.findActiveBetweenDate").setParameter("date1", date1).setParameter("date2", date2).getResultList();
			this.SetUserRegistered(list);
			return list;
		} catch (Exception e) {
			if(!e.toString().contains("did not retrieve any entities")) {
				CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, MyMassageProperties.PARAM_SYSTEM_USER);
			}
			return null;
		}
	}
	
	@Override
	public List<Massage> getMassageAvailableBetweenDates(Date date1, Date date2) {
		try {
			List<Massage> list = em.createNamedQuery("Massage.findAvailableBetweenDate").setParameter("date1", date1).setParameter("date2", date2).getResultList();
			return list;
		} catch (Exception e) {
			if(!e.toString().contains("did not retrieve any entities")) {
				CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, MyMassageProperties.PARAM_SYSTEM_USER);
			}
			return null;
		}
	}

	
	@Override
	public Massage getMassageByDate(Date date) {
		try {
			Massage m = (Massage) em.createNamedQuery("Massage.findByDate").setParameter("date", date).getSingleResult();
			this.SetUserRegistered(m);
			return m; 
		} catch (Exception e) {
			if(!e.toString().contains("did not retrieve any entities")) {
				CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, MyMassageProperties.PARAM_SYSTEM_USER);
			}			
			return null;
		}
	}


	@Override
	public Massage saveMassage(Massage massage) {
		if (massage.getId() == null || massage.getId() == 0) {
			em.persist(massage);
		} else {
			em.merge(massage);
		}
		em.flush();
		// récupération du user pour mettre les id à jour
		massage = this.getMassageByDate(massage.getDate());
		return massage;
	}


	@Override
	public List<Massage> getHighlightedMassage() {
		try {
			List<Massage> list = em.createNamedQuery("Massage.findByHighlighted").setMaxResults(1).getResultList();
			this.SetUserRegistered(list);
			return list;
		} catch (Exception e) {
			if(!e.toString().contains("did not retrieve any entities")) {
				CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, MyMassageProperties.PARAM_SYSTEM_USER);
			}
			return null;
		}
	}

	@Override
	public int getMonthDifferenceWithLastMassage() {
		int nb = 0;
		try
		{
			// [ignore]
			nb = Integer.valueOf(em.createNativeQuery("select TIMESTAMPDIFF(month, now(), max(date)) + 1 from massage").getSingleResult().toString());
		} catch (Exception e)
		{
			nb = 1;
		}
		return nb;
	}

	@Override
	public boolean isMassageFullForDate(Date uneDate) {
		int nb = 0;
		try
		{
			// [ignore]
			nb = Integer.valueOf(em.createNativeQuery("SELECT (select count(*) from massage where date(date) = '" + LoroDate.getStringFromDate(uneDate, "yyyy-MM-dd") + "') - (select count(*) from massage where date(date) = '" + LoroDate.getStringFromDate(uneDate, "yyyy-MM-dd") + "' and isVacant = 1)").getSingleResult().toString());
		} catch (Exception e)
		{
			nb = 1;
		}
		if (nb == 0)
			return true;
		else
			return false;
	}

}
