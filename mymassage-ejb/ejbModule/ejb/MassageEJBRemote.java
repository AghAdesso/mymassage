package ejb;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import model.Massage;

@Remote
public interface MassageEJBRemote {

	public List<Massage> getAllMassage();
	
	public List<Massage> getMessageBetweenDates(Date date1, Date date2);
	
	public List<Massage> getMassageAvailableBetweenDates(Date date1, Date date2);
	
	public Massage getMassageByDate(Date date);
	
	
	public Massage saveMassage(Massage massage) ;
	
	public List<Massage> getHighlightedMassage();
	
	public int getMonthDifferenceWithLastMassage();
	
	public boolean isMassageFullForDate(Date uneDate);
}
