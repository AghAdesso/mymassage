package ejb;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import model.Massage;
import model.Registration;

@Remote
public interface RegistrationEJBRemote {
	
	public Registration getRegistrationByMassage(Massage massage);
	
	public List<Registration> getAllRegistration();
	
	public Registration saveRegistration(Registration registration);
	
	public Registration findByUser(String lastName, String firstName);
	
	public List<Registration> findByReminder(Date date1, Date date2);
}
