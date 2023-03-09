package beans;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.json.JSONArray;
import org.primefaces.model.UploadedFile;

import ejb.MassageEJBRemote;
import ejb.RegistrationEJBRemote;
import excel.ExcelImportPlanning;
import lombok.Getter;
import lombok.Setter;
import model.LogSeverityEnum;
import model.LogTypeEnum;
import model.Massage;
import model.Registration;
import util.CustomLogger;
import util.LoroDate;
import util.MyMassageMessage;
import util.MyMassageProperties;

@ManagedBean(name = "massageBean")
@ViewScoped
@Getter
@Setter
public class MassageBean extends RootBean {

	private List<Massage> listMassages;
	private Massage selectedMassage;
	private Date datePlanning;
	private Calendar modifiedCalendar;
	private Registration registration;
	private JSONArray highlightDateList;
	private UploadedFile file;
	
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@EJB
	private MassageEJBRemote massageEJB;
	
	@EJB
	private RegistrationEJBRemote registrationEJB;
	
	public MassageBean() {
		this.listMassages = null;
		this.selectedMassage = null;
		this.datePlanning = new Date();
		this.modifiedCalendar = Calendar.getInstance();
		this.highlightDateList = new JSONArray();
		this.file = null;
	}
		
	
	@PostConstruct
	public void init() {	
		// if it's not an admin or a gestionnaire
		if(!loginBean.getRole().equals(MyMassageProperties.ROLE_ADMIN) && !loginBean.getRole().equals(MyMassageProperties.ROLE_GESTIONNAIRE)) {
			try {
				navigation.addMessage(MyMassageMessage.MESSAGE_ERROR, MyMassageMessage.MESSAGE_ACCESS_DENIED, FacesMessage.SEVERITY_FATAL, null);
				FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.redirectToIndexFullUrl());
			} catch (IOException e) {
				CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.FATAL, LogTypeEnum.APPLICATION, e, MyMassageProperties.PARAM_SYSTEM_USER);
			}
		}
		
		this.initMassageList();
	}
	
	public void initMassageList() {
		// set the time of date at hour 0 and 23
		this.modifiedCalendar.setTime(this.datePlanning);
		this.modifiedCalendar.set(Calendar.HOUR_OF_DAY, 0);
		Calendar dateEvening = (Calendar) this.modifiedCalendar.clone();
		dateEvening.set(Calendar.HOUR_OF_DAY, 23);
		
		//get all the massage for the date selected
		this.listMassages = massageEJB.getMessageBetweenDates(this.modifiedCalendar.getTime(), dateEvening.getTime());
		
		//set the date to now
		this.modifiedCalendar.setTime(new Date());
		this.modifiedCalendar.set(Calendar.MONTH, -3);
		//calendar day to highlight lines
		dateEvening.setTime(new Date());
		// met les jours en vert jusqu'au dernier massag enregistré
		dateEvening.add(Calendar.MONTH, massageEJB.getMonthDifferenceWithLastMassage());
		// set the last day of the month
		dateEvening.set(Calendar.DAY_OF_MONTH, modifiedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		//days to highlight for the next 3 months
		List<Massage> massageToHighlightList = massageEJB.getMessageBetweenDates(modifiedCalendar.getTime(), dateEvening.getTime());
		List<String> dateToHihglight = new ArrayList<String>();
		SimpleDateFormat formatDays = new SimpleDateFormat("yyyy-MM-dd");
		boolean dateToAdd = true;
		for(int i = 0; i < massageToHighlightList.size(); i++) {
			// if the date doesn't exist in the list of the days to highlight 
			// add it
			dateToAdd = true;
			String dayToAdd = formatDays.format(massageToHighlightList.get(i).getDate());
			for(int a = 0; a < dateToHihglight.size(); a++) {	
				String dayToCompare = dateToHihglight.get(a);
				if(dayToAdd.equals(dayToCompare)) {
					dateToAdd = false;
				}
			}
			if(dateToAdd) {
				dateToHihglight.add(dayToAdd);
				this.highlightDateList.put(dayToAdd);
			}
		}
	}
	
	public void dateChange() {
		this.initMassageList();
	}
	
	public void addDate() {
		this.selectedMassage = new Massage(this.datePlanning);
		this.registration = new Registration();
		this.initMassageList();
	}
	
	public void saveDate() throws Exception {
		
		//if it's a new date
		//check if the date exists already in the DB
		if (this.selectedMassage.getId() == null || this.selectedMassage.getId() == 0) {		
			if(massageEJB.getMassageByDate(this.selectedMassage.getDate()) != null) {
				Massage massage = massageEJB.getMassageByDate(this.selectedMassage.getDate());
				if(massage.getIsActive() == false) {
					massage.setIsActive(true);
					massage.setIsVacant(true);
					this.selectedMassage = massage;
				} else {
					CustomLogger.log("Date Save", LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, "The date already exists", null);
				}				
			}			
		}
		this.selectedMassage.setIsVacant(false);
		this.selectedMassage = this.massageEJB.saveMassage(this.selectedMassage);
		//if assigns a person to a new massage add the id of the massage
		if(this.registration.getMassage() == null) {
			this.registration.setMassage(this.selectedMassage);
		}
		
		//check if the registration has been completed
		//if (this.registration.getFirstName() != "" && this.registration.getLastName() != "" 
					//&& this.registration.getPhoneNumber() != 0) {
			this.registration = this.registrationEJB.saveRegistration(this.registration);
		//}

		this.initMassageList();

	}
	
	public void cancelDate() {
		this.initMassageList();
	}
	
	public void initRegistration() {
		// get the registration for the appointment
		this.registration = registrationEJB.getRegistrationByMassage(this.selectedMassage);
		
		// if the appointment doesn't have a registration, create a new one
		if(this.registration == null) {
			this.registration = new Registration(this.selectedMassage);
		}
	}
	
	public void importDate() {
		try {
			List<String> listErrors = new ArrayList<String>();
			List<Massage> massagesToSave = ExcelImportPlanning.exportPlanning(this.file.getInputstream());
			for(Massage massage : massagesToSave) {
				Massage m = massageEJB.getMassageByDate(massage.getDate());
				if (m != null)
				{
					listErrors.add(LoroDate.getStringFromDate(m.getDate(), LoroDate.FORMAT_TIMESTAMP));
				}
				else
				{
					this.massageEJB.saveMassage(massage);
				}
			}
			this.initMassageList();
			if (listErrors.size() != 0)
			{
				String message = "";
				for (String s : listErrors)
				{
					message += s + ", ";
				}
				navigation.addMessage(MyMassageMessage.MESSAGE_ERROR, "Il existe déjà un rdv pour les dates : " + message , FacesMessage.SEVERITY_FATAL, null);
			}
				
		} catch (Exception e) {
			CustomLogger.log("ExcelImport", LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, null);
		}
	}
	
	public void cancelImport() {
		this.initMassageList();
	}
}
