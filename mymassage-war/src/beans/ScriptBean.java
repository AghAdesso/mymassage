package beans;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;

import ejb.RegistrationEJBRemote;
import lombok.Getter;
import lombok.Setter;
import model.LogSeverityEnum;
import model.LogTypeEnum;
import model.Registration;
import util.CustomLogger;
import util.MyMassageProperties;

@ManagedBean(name = "scriptBean")
@ViewScoped
@Getter
@Setter
public class ScriptBean {

	private String message;
	private List<Registration> registrationList;
	
	@EJB
	private RegistrationEJBRemote registrationEJB;
	
	public ScriptBean() {
		this.message = "";
		this.registrationList = null;
	}
	
	@PostConstruct
	private void init() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		Calendar todayNight = (Calendar) today.clone();
		todayNight.set(Calendar.HOUR_OF_DAY, 23);
		this.registrationList = this.registrationEJB.findByReminder(today.getTime(), todayNight.getTime());
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		for(Registration registration : this.registrationList) {
			String to = registration.getFirstName() + "." + registration.getLastName() + "@loro.ch";
			String subject = "Rappel: Massage assis aujourd'hui à " + dateFormat.format(registration.getMassage().getDate()) + ".";
			this.sendEmail(to, "", "", subject, "Ceci est un rappel automatique, Merci de ne pas répondre", null);
		}
	}
	
	private void sendEmail(String to, String cc, String bcc, String subject, String message, String filePath) {
		try {
			HtmlEmail email = new HtmlEmail();
			if(!message.equals("") && message != null) {
				email.setHtmlMsg(message);
			}
			email.setCharset("UTF-8");
			email.setHostName(MyMassageProperties.EMAIL_HOST);
			// to
			String[] listToString = to.split(";");
			List<InternetAddress> listTo = new ArrayList<InternetAddress>();
			for (int i = 0; i < listToString.length; i++) {
				listTo.add(new InternetAddress(listToString[i]));
			}
			email.setTo(listTo);
			// cc
			if (cc != null && !cc.equals("")) {
				String[] listCcString = cc.split(";");
				List<InternetAddress> listCc = new ArrayList<InternetAddress>();
				for (int i = 0; i < listCcString.length; i++) {
					listCc.add(new InternetAddress(listCcString[i]));
				}
				email.setCc(listCc);
			}
			// bcc
			if (bcc != null && !bcc.equals("")) {
				String[] listBccString = bcc.split(";");
				List<InternetAddress> listBcc = new ArrayList<InternetAddress>();
				for (int i = 0; i < listBccString.length; i++) {
					listBcc.add(new InternetAddress(listBccString[i]));
				}
				email.setBcc(listBcc);
			}
			email.setFrom(MyMassageProperties.EMAIL_FROM_EMAIL, MyMassageProperties.EMAIL_FROM_NAME);
			email.setSubject(subject);
			if (filePath != null) {
				EmailAttachment attachment = new EmailAttachment();
				attachment.setPath(filePath);
				attachment.setDisposition(EmailAttachment.ATTACHMENT);
				attachment.setDescription(filePath.split("/")[filePath.split("/").length - 1].replaceAll("_", " "));
				attachment.setName(filePath.split("/")[filePath.split("/").length - 1].replaceAll("_", " "));
				email.attach(attachment);
			}
			// send the email
			email.send();
		} catch (Exception e) {
			CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.FATAL, LogTypeEnum.APPLICATION, e, null);
		}
	}
}
