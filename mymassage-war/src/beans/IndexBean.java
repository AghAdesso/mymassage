package beans;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.context.RequestContext;
import org.primefaces.json.JSONArray;

import ejb.MassageEJBRemote;
import ejb.ParameterEJBRemote;
import ejb.RegistrationEJBRemote;
import excel.ExcelExportPlanningMonth;
import lombok.Getter;
import lombok.Setter;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.core.enumeration.service.SendInvitationsMode;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import model.LogSeverityEnum;
import model.LogTypeEnum;
import model.Massage;
import model.Parameter;
import model.Registration;
import pdf.PDFGenerator;
import pdf.PDFRegistration;
import util.CustomLogger;
import util.MyMassageMessage;
import util.MyMassageProperties;


@ManagedBean(name = "indexBean")
@ViewScoped
@Getter
@Setter
public class IndexBean extends RootBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_NUMBER = 313;
	private List<Massage> massageList;
	private Massage selectedMassage;
	private Calendar calendarSelected;
	private Date dateSelected;
	private Registration userRegistration;
	private Registration registration;
	private Parameter daysToHighlight;
	private Parameter daysToCancel;
	private Registration commentRegistration;
	private SimpleDateFormat format;
	private Massage massageHighlighted;
	private Date today;
	private JSONArray highlightDateList;
	private JSONArray highlightDateFullList;
	private List<SelectItem> monthsList;
	private List<SelectItem> yearList; 
	private String month;
	private String year;
	
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@EJB
	MassageEJBRemote massageEJB;
	
	@EJB
	RegistrationEJBRemote registrationEJB;
	
	@EJB
	ParameterEJBRemote parameterEJB;
	
	public IndexBean()  {
		this.massageList = new ArrayList<Massage>();
		this.selectedMassage = null;
		this.calendarSelected = Calendar.getInstance();
		
		//set the hour at 0 to get all the appointment for today
		this.calendarSelected.set(Calendar.HOUR_OF_DAY, 0);
		// for the calendar view
		this.dateSelected = this.calendarSelected.getTime();
		this.registration = null;
		this.userRegistration = null;
		this.commentRegistration = new Registration();
		this.format = new SimpleDateFormat("dd.MM.yyyy à HH:mm");
		this.massageHighlighted = null;
		this.today = this.calendarSelected.getTime();
		this.highlightDateList = new JSONArray();
		this.highlightDateFullList = new JSONArray();
		this.monthsList = new ArrayList<SelectItem>();
		this.yearList = new ArrayList<SelectItem>();
	}
	
	@PostConstruct
	public void init() {
		this.initalizeYearAndMonthList();
		this.year = Integer.toString(this.calendarSelected.get(Calendar.YEAR));
		this.month = (String) this.monthsList.get(this.calendarSelected.get(Calendar.MONTH)).getValue();
		
		this.daysToCancel = this.parameterEJB.getParameterByKey(Parameter.DAYS_TO_CANCEL);
		this.daysToHighlight = this.parameterEJB.getParameterByKey(Parameter.DAYS_TO_HIGHLIGHT);
		// get the highlighted massages
		List<Massage> highlightedMassages = this.massageEJB.getHighlightedMassage();
		// if the list is not empty, get the first one to show on page
		if(highlightedMassages != null) {
			if(!highlightedMassages.isEmpty()) {
				this.massageHighlighted = highlightedMassages.get(0);
			}
		}

		// clear the list
		this.massageList.clear();
		this.userRegistration = this.registrationEJB.findByUser(this.loginBean.getLastName(), this.loginBean.getFirstName());
		
		// get the date at 0 and 23 hour
		Calendar calendarDateSelected = Calendar.getInstance();
		calendarDateSelected.setTime(this.dateSelected);
		Calendar calendarDateSelctedNight = (Calendar) calendarDateSelected.clone();
		calendarDateSelected.set(Calendar.HOUR_OF_DAY,0);
		calendarDateSelctedNight.set(Calendar.HOUR_OF_DAY, 23);
		
		// get all the massage for today and the following
		this.massageList = massageEJB.getMessageBetweenDates(calendarDateSelected.getTime(), calendarDateSelctedNight.getTime());
		calendarDateSelctedNight.setTime(this.today);
		// met les jours en vert jusqu'au dernier massag enregistré
		calendarDateSelctedNight.add(Calendar.MONTH, massageEJB.getMonthDifferenceWithLastMassage());
		// set the last day of the month
		calendarDateSelctedNight.set(Calendar.DAY_OF_MONTH, calendarDateSelctedNight.getActualMaximum(Calendar.DAY_OF_MONTH));
		//days to highlight for the next 3 months
		List<Massage> massageToHighlightList = massageEJB.getMessageBetweenDates(this.today, calendarDateSelctedNight.getTime());
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
			
				//si la date est pleine on la met dans liste des dates non disponibles		
				Calendar temp0 = Calendar.getInstance();
				temp0.setTime(massageToHighlightList.get(i).getDate());
				Calendar temp24 = (Calendar) temp0.clone();
				temp0.set(Calendar.HOUR_OF_DAY,0);
				temp24.set(Calendar.HOUR_OF_DAY, 23);
				List<Massage> listeMassage = massageEJB.getMassageAvailableBetweenDates(temp0.getTime(), temp24.getTime());
				if (listeMassage.size() == 0)
					this.highlightDateFullList.put(dayToAdd);
				else
					this.highlightDateList.put(dayToAdd);
					
			}
		}
	}	
	
	public void registrate(Massage massage) {
		RequestContext context = RequestContext.getCurrentInstance();
		if(!this.loginBean.getIsLogged()) {
			navigation.addMessage(MyMassageMessage.MESSAGE_WARNING, MyMassageMessage.MESSAGE_LOGIN_FIRST, FacesMessage.SEVERITY_WARN, null);
		} else {
			//check of the user has already an appointment
			if(this.userRegistration == null) {
				context.execute(("PF('indexDialogWv').show()"));
				this.selectedMassage = massage;
				this.registration = new Registration(massage);
				this.registration.setFirstName(this.loginBean.getFirstName());
				this.registration.setLastName(this.loginBean.getLastName());
				try {
					this.registration.setPhoneNumber(Integer.parseInt(this.loginBean.getPhoneNumber().substring(10)));
				} catch (Exception e)
				{
					this.registration.setPhoneNumber(IndexBean.DEFAULT_NUMBER);
				}
				
			} else {
				context.execute(("PF('indexDialogWv').hide()"));
				navigation.addMessage(MyMassageMessage.MESSAGE_WARNING, MyMassageMessage.MESSAGE_REGISTRATION_DENIED, FacesMessage.SEVERITY_WARN, null);
			}		
		}
	}
	
	public void cancelRegistration() {
		this.init();
		this.registration = null;
	}
	
	public void saveRegistration() {
		String dateFormated = "";
		// if the user hasn't a registration yet, save it else show a warning message
		if(this.userRegistration == null) {
			this.registration.getMassage().setIsVacant(false);
			this.registration.getMassage().setIsHighlighted(false);
			Calendar selectedMassageCalendar = Calendar.getInstance();
			selectedMassageCalendar.setTime(this.selectedMassage.getDate());	
			boolean savedToOutlook = false;
			try {
				
				// send an appointment to the user
				Appointment appointment = new Appointment(this.loginBean.getService());
	
				// Set the properties on the appointment object to create the appointment.
				appointment.setSubject("Massage assis");
				MessageBody mb = new MessageBody(this.parameterEJB.getParameterByKey(Parameter.BODY_EMAIL).getParameterValue());
				appointment.setBody(mb);
				appointment.setStart(selectedMassageCalendar.getTime());
				int lengthMassage = Integer.parseInt(this.parameterEJB.getParameterByKey(Parameter.MASSAGE_LENGTH).getParameterValue());
				dateFormated = this.format.format(selectedMassageCalendar.getTime()).toString();
				selectedMassageCalendar.add(Calendar.MINUTE, lengthMassage);
				appointment.setEnd(selectedMassageCalendar.getTime());
				appointment.setLocation(this.parameterEJB.getParameterByKey(Parameter.MASSAGE_ROOM).getParameterValue());
				
				// reminder 15 minutes before
				selectedMassageCalendar.add(Calendar.MINUTE, ((-1*lengthMassage) - 15));
				appointment.setReminderDueBy(selectedMassageCalendar.getTime());
	
				// Save the appointment to your calendar.
				appointment.save(SendInvitationsMode.SendToNone);
				
				this.registration.setAppointmentId(appointment.getId().getUniqueId());
				savedToOutlook = true;
			} catch (Exception e) {
				CustomLogger.log(this.getClass().getSimpleName() + " - " + Thread.currentThread().getStackTrace()[1].getMethodName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e.toString() , MyMassageProperties.PARAM_SYSTEM_USER);
			}
			
			// save the massage and the registration
			this.massageEJB.saveMassage(this.registration.getMassage());
			this.registrationEJB.saveRegistration(this.registration);
			this.userRegistration = this.registration;
			this.registration = new Registration();
			this.init();
			if (!savedToOutlook)
				navigation.addMessage(MyMassageMessage.MESSAGE_SUCCESS, MyMassageMessage.MESSAGE_REGISTRATION_SUCCESS + dateFormated + MyMassageMessage.MESSAGE_REGISTRATION_SUCCESS_BUT, FacesMessage.SEVERITY_INFO, null);
			else
				navigation.addMessage(MyMassageMessage.MESSAGE_SUCCESS, MyMassageMessage.MESSAGE_REGISTRATION_SUCCESS + dateFormated, FacesMessage.SEVERITY_INFO, null);
		} else {
			navigation.addMessage(MyMassageMessage.MESSAGE_WARNING, MyMassageMessage.MESSAGE_REGISTRATION_DENIED, FacesMessage.SEVERITY_WARN, null);
		}
	}
	
	public void cancelMassage() {
		RequestContext context = RequestContext.getCurrentInstance();
		Date dateNow = Calendar.getInstance().getTime();
		
		// the diff is in milliseconds
		long diff = this.userRegistration.getMassage().getDate().getTime() - dateNow.getTime();
		
		// convert the diff in days
		diff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

		// if the diff are less than the parameter daysToCancel, show a dialog with the informations of the substitute and a massage (s)he can't cancel 
		if(diff <= Integer.parseInt(this.daysToCancel.getParameterValue())) {
			context.execute("PF('indexCommentDialogWv').show()");
			// if the diff are less than the param daysToHighlight, set isHighlight to true and cancel
		} else if(diff <= Integer.parseInt(this.daysToHighlight.getParameterValue())) {
			this.userRegistration.getMassage().setIsHighlighted(true);
			this.userRegistration.getMassage().setIsVacant(true);
			this.userRegistration.setIsActive(false);
			this.massageEJB.saveMassage(this.userRegistration.getMassage());
			this.registrationEJB.saveRegistration(this.userRegistration);
			navigation.addMessage(MyMassageMessage.MESSAGE_SUCCESS, MyMassageMessage.MESSAGE_REGISTRATION_CANCEL, FacesMessage.SEVERITY_INFO, null);
			this.cancelAppointmentOutlook();
			// just cancel
		} else {
			this.userRegistration.setIsActive(false);
			this.userRegistration.getMassage().setIsVacant(true);
			this.massageEJB.saveMassage(this.userRegistration.getMassage());
			this.registrationEJB.saveRegistration(this.userRegistration);
			navigation.addMessage(MyMassageMessage.MESSAGE_SUCCESS, MyMassageMessage.MESSAGE_REGISTRATION_CANCEL, FacesMessage.SEVERITY_INFO, null);
			this.cancelAppointmentOutlook();
		}
		this.init();
	}
	
	public void cancelRegistrationComment() {
		this.commentRegistration = new Registration();
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('indexCommentDialogWv').hide()");
		this.init();
	}
	
	public void saveRegistrationComment() {
		// never trust the user, force him to fill the inputs and construct the comment after
		String comment = "Prénom : " + this.commentRegistration.getFirstName() + "; Nom : " + this.commentRegistration.getLastName() + 
				"; Téléphone : " + this.commentRegistration.getPhoneNumber();
		this.userRegistration.setComment(comment);
		// isCanceled to true so (s)he can register again
		this.userRegistration.setIsCanceled(true);
		this.registrationEJB.saveRegistration(userRegistration);
		this.cancelAppointmentOutlook();
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('indexCommentDialogWv').hide()");
		this.init();
	}
	
	public void dateChange() {
		this.init();
	}	
	
	public void generateDayRegistrationPDF() {
		
		//if no massage exist for the date, show a message
		if(this.massageList != null && !this.massageList.isEmpty()) {
			try {
			HttpServletResponse response = ((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse());
			// list to be tranformed to array
			List<PDFRegistration> registrationList = new ArrayList<PDFRegistration>();
			// date formats
			SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy");
			SimpleDateFormat hour = new SimpleDateFormat("HH:mm");
			// browse the table of the massageList of the selectedDate
			for(Massage massage : this.massageList) {
				// create a object containing the informations for the PDF
				PDFRegistration pdfRegistration = new PDFRegistration();
				// check if a registration exits for the date
				Registration registration = this.registrationEJB.getRegistrationByMassage(massage);
				// if a registration exits, fill the object with the infos
				if(registration != null) {
					pdfRegistration.setComment(registration.getComment());
					pdfRegistration.setLastName(registration.getLastName());
					pdfRegistration.setFirstName(registration.getFirstName());
					pdfRegistration.setPhoneNumber(String.valueOf(registration.getPhoneNumber()));
				}
				// format the date for the PDF
				pdfRegistration.setDateSelected(date.format(massage.getDate()));
				pdfRegistration.setHour(hour.format(massage.getDate()));
				// add to the list
				registrationList.add(pdfRegistration);
			}
			// create an array who will be passed to the PDF
			PDFRegistration[] registrationRecord = new PDFRegistration[registrationList.size()];
			// generate the array from the list
			registrationRecord = registrationList.toArray(registrationRecord);
			// initialize the class who will generate the PDF
			
				if (new File(MyMassageProperties.PDF_TEMPLATE_REGISTER_DAY_FILE1).exists()) {
					//CustomLogger.log("Export PDF", LogSeverityEnum.ERROR, LogTypeEnum.APPLICATION,MyMassageProperties.PDF_TEMPLATE_REGISTER_DAY_FILE1, MyMassageProperties.PARAM_SYSTEM_USER );
					PDFGenerator.printReport(response, registrationRecord, MyMassageProperties.PDF_TEMPLATE_REGISTER_DAY_FILE1);
				} else {
					//CustomLogger.log("Export PDF", LogSeverityEnum.ERROR, LogTypeEnum.APPLICATION, MyMassageProperties.PDF_TEMPLATE_REGISTER_DAY_FILE2, MyMassageProperties.PARAM_SYSTEM_USER);
					PDFGenerator.printReport(response, registrationRecord, MyMassageProperties.PDF_TEMPLATE_REGISTER_DAY_FILE2);
				}
			} catch (Exception e){
				CustomLogger.log("IndexBean", LogSeverityEnum.ERROR, LogTypeEnum.APPLICATION, e, MyMassageProperties.PARAM_SYSTEM_USER);
				navigation.addMessage(MyMassageMessage.MESSAGE_ERROR, MyMassageMessage.MESSAGE_ERROR, FacesMessage.SEVERITY_ERROR, e.toString());
			}
			
	    } else {
	    	navigation.addMessage(MyMassageMessage.MESSAGE_WARNING, MyMassageMessage.MESSAGE_EXPORT_NODATA, FacesMessage.SEVERITY_WARN, null);
	    }
	}
	
	public void exportPlanning() {
		Calendar monthFirstDay = Calendar.getInstance();
		monthFirstDay.set(Calendar.MONTH, Integer.parseInt(this.month));
		monthFirstDay.set(Calendar.YEAR, Integer.parseInt(this.year));
		Calendar monthLastDay = (Calendar) monthFirstDay.clone();
		monthFirstDay.set(Calendar.DAY_OF_MONTH, 1);
		monthLastDay.set(Calendar.DAY_OF_MONTH, monthLastDay.getActualMaximum(Calendar.DAY_OF_MONTH));
		List<Massage> massageMonthList = this.massageEJB.getMessageBetweenDates(monthFirstDay.getTime(), monthLastDay.getTime());
		String filename = ExcelExportPlanningMonth.exportPlanning(monthFirstDay, massageMonthList);
		if (filename == null) {
			navigation.addMessage(MyMassageMessage.MESSAGE_ERROR, MyMassageMessage.MESSAGE_FILE_EXCEL, FacesMessage.SEVERITY_ERROR, null);
			CustomLogger.log(this.getClass().getSimpleName() + " - " + Thread.currentThread().getStackTrace()[1].getMethodName(), LogSeverityEnum.FATAL, LogTypeEnum.APPLICATION, MyMassageMessage.MESSAGE_FILE_EXCEL, null);
		} else {
			// open file to display
			BufferedInputStream bIn = null;
			try {
				HttpServletResponse response = ((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse());
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				String month = new SimpleDateFormat("MMM").format(monthFirstDay.getTime());
				response.setHeader("content-disposition", "attachment;filename=Planning-" + month + ".xlsx");
				bIn = new BufferedInputStream(new FileInputStream(filename));
				int rLength = -1;
				byte[] buffer = new byte[1000];
				while ((rLength = bIn.read(buffer, 0, 100)) != -1)
					response.getOutputStream().write(buffer, 0, rLength);
				FacesContext.getCurrentInstance().responseComplete();
			} catch (Exception e) {
				navigation.addMessage(MyMassageMessage.MESSAGE_ERROR, e.toString(), FacesMessage.SEVERITY_ERROR, null);
				CustomLogger.log(this.getClass().getSimpleName() + " - " + Thread.currentThread().getStackTrace()[1].getMethodName(), LogSeverityEnum.FATAL, LogTypeEnum.APPLICATION, e.toString(), null);
			} finally {
				try {
					bIn.close();
				} catch (Exception e) {
					CustomLogger.log(this.getClass().getSimpleName() + " - " + Thread.currentThread().getStackTrace()[1].getMethodName(), LogSeverityEnum.ERROR, LogTypeEnum.APPLICATION, "Erreur fermeture fichier excel : " + e, null);
				}
			}
		}
	}
	
	public void prepareEmail() {
		if(this.massageList != null && !this.massageList.isEmpty()) {
			try {
				String subject = "";
				String object = "";
				String sendTo = "";
				String sendCc = "";
				String sendBcc = "";
				String filePath = "";
				this.calendarSelected.setTime(this.dateSelected);
				String dateFormated = new SimpleDateFormat("dd.MM.yyyy").format(this.calendarSelected.getTime());
				
				subject = "Liste des réservations du " + dateFormated + ".";
				object = "Bonjour, <br><br> Veuillez trouver ci-joint la liste des réservations du " + dateFormated + ". <br><br> Je vous adresse mes salutations les meilleures. <br><br> MyMassage";
				
				Parameter parameter = this.parameterEJB.getParameterByKey(Parameter.MASSEUR_EMAIL);
				sendTo = parameter.getParameterValue();
				
				// list to be tranformed to array
				List<PDFRegistration> registrationList = new ArrayList<PDFRegistration>();
				// date formats
				SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy");
				SimpleDateFormat hour = new SimpleDateFormat("HH:mm");
				// browse the table of the massageList of the selectedDate
				for(Massage massage : this.massageList) {
					// create a object containing the informations for the PDF
					PDFRegistration pdfRegistration = new PDFRegistration();
					// check if a registration exits for the date
					Registration registration = this.registrationEJB.getRegistrationByMassage(massage);
					// if a registration exits, fill the object with the infos
					if(registration != null) {
						pdfRegistration.setComment(registration.getComment());
						pdfRegistration.setLastName(registration.getLastName());
						pdfRegistration.setFirstName(registration.getFirstName());
						pdfRegistration.setPhoneNumber(String.valueOf(registration.getPhoneNumber()));
					}
					// format the date for the PDF
					pdfRegistration.setDateSelected(date.format(massage.getDate()));
					pdfRegistration.setHour(hour.format(massage.getDate()));
					// add to the list
					registrationList.add(pdfRegistration);
				}
				// create an array who will be passed to the PDF
				PDFRegistration[] registrationRecord = new PDFRegistration[registrationList.size()];
				// generate the array from the list
				registrationRecord = registrationList.toArray(registrationRecord);
				
				if (new File(MyMassageProperties.PDF_TEMPLATE_REGISTER_DAY_FILE1).exists()) {
					filePath = PDFGenerator.exportReport(registrationRecord, MyMassageProperties.PDF_TEMPLATE_REGISTER_DAY_FILE1);
				} else {
					filePath = PDFGenerator.exportReport(registrationRecord, MyMassageProperties.PDF_TEMPLATE_REGISTER_DAY_FILE2);
				}
				
				this.sendEmail(sendTo, sendCc, sendBcc, subject, object, filePath);
			} catch (Exception e) {
				CustomLogger.log(this.getClass().getSimpleName() + " - " + Thread.currentThread().getStackTrace()[1].getMethodName(), LogSeverityEnum.FATAL, LogTypeEnum.APPLICATION, e, null);
				navigation.addMessage(MyMassageMessage.MESSAGE_ERROR, MyMassageMessage.MESSAGE_ERROR_SEND_EMAIL, FacesMessage.SEVERITY_ERROR, null);
			}
		} else {
			navigation.addMessage(MyMassageMessage.MESSAGE_WARNING, MyMassageMessage.MESSAGE_EMAIL_NODATA, FacesMessage.SEVERITY_WARN, null);
		}
	}
	
	/* PRIVATE METHODS */
	
	private void initalizeYearAndMonthList() {
		if(this.monthsList.isEmpty()) {
			this.monthsList.add(new SelectItem("0","Janvier"));
			this.monthsList.add(new SelectItem("1","Février" ));
			this.monthsList.add(new SelectItem("2","Mars"));
			this.monthsList.add(new SelectItem("3","Avril"));
			this.monthsList.add(new SelectItem("4","Mai"));
			this.monthsList.add(new SelectItem("5","Juin"));
			this.monthsList.add(new SelectItem("6","Juillet"));
			this.monthsList.add(new SelectItem("7","Août"));
			this.monthsList.add(new SelectItem("8","Septembre"));
			this.monthsList.add(new SelectItem("9","Octobre"));
			this.monthsList.add(new SelectItem("10","Novembre"));
			this.monthsList.add(new SelectItem("11","Décembre"));
		}
		if(this.yearList.isEmpty()) {
			int year = this.calendarSelected.get(Calendar.YEAR);
			this.yearList.add(new SelectItem(Integer.toString((year - 1)),Integer.toString((year - 1))));
			this.yearList.add(new SelectItem(Integer.toString(year),Integer.toString(year)));
			this.yearList.add(new SelectItem(Integer.toString((year + 1)),Integer.toString(year + 1)));
		}	
	}
	
	private void sendEmail(String to, String cc, String bcc, String subject, String message, String filePath) {
		try {
			EmailMessage email = new EmailMessage(this.loginBean.getService());

			MessageBody messageBody = new MessageBody();
			messageBody.setBodyType(BodyType.HTML);
			messageBody.setText(message);
			email.setBody(messageBody);

			// to
			String[] listToString = to.split(";");
			for (int i = 0; i < listToString.length; i++) {
				email.getToRecipients().add(listToString[i]);
			}
			// cc
			if (cc != null && !cc.equals("")) {
				String[] listCcString = cc.split(";");
				for (int i = 0; i < listCcString.length; i++) {
					email.getCcRecipients().add(listCcString[i]);
				}
			}
			
			// bcc
			if (bcc != null && !bcc.equals("")) {
				String[] listBccString = bcc.split(";");
				for (int i = 0; i < listBccString.length; i++) {
					email.getBccRecipients().add(listBccString[i]);
				}
			}
			
			email.setFrom(new EmailAddress(this.loginBean.getAdUser().getMail()));
			
			email.setSubject(subject);
			if (filePath != null) {
				email.getAttachments().addFileAttachment(filePath);
			}
			// send the email
			email.sendAndSaveCopy();
		} catch (Exception e) {
			CustomLogger.log(this.getClass().getSimpleName() + " - " + Thread.currentThread().getStackTrace()[1].getMethodName(), LogSeverityEnum.FATAL, LogTypeEnum.APPLICATION, e, null);
			navigation.addMessage(MyMassageMessage.MESSAGE_ERROR, MyMassageMessage.MESSAGE_ERROR_SEND_EMAIL, FacesMessage.SEVERITY_ERROR, null);
		}
	}

	private void cancelAppointmentOutlook() {
		// if the user has an appointment in outlook calendar
		if(!this.userRegistration.getAppointmentId().equals("")) {
			try {
				Appointment appointment = Appointment.bind(this.loginBean.getService(), new ItemId(this.userRegistration.getAppointmentId()));
				appointment.delete(DeleteMode.MoveToDeletedItems);
			} catch (Exception e) {
				CustomLogger.log(this.getClass().getSimpleName() + " - " + Thread.currentThread().getStackTrace()[1].getMethodName(), LogSeverityEnum.FATAL, LogTypeEnum.APPLICATION, e.toString(), null);
			}
		}
	}
}
	
