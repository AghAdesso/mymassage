package beans;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import ejb.LogEJBRemote;
import lombok.Getter;
import lombok.Setter;
import model.Log;
import model.LogSeverityEnum;
import model.LogTypeEnum;
import util.CustomLogger;
import util.MyMassageMessage;
import util.MyMassageProperties;

@ManagedBean(name = "logBean")
@ViewScoped
@Getter
@Setter
public class LogBean extends RootBean {

	private List<Log> logsList;
	private List<Log> filteredLogs;
	
	@EJB
	private LogEJBRemote logEJB;
	
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;	
	
	public LogBean() {
		this.logsList = null;
	}
	
	@PostConstruct
	public void init() {
		// if it's not an admin
		if(!loginBean.getRole().equals(MyMassageProperties.ROLE_ADMIN)) {
			try {
				navigation.addMessage(MyMassageMessage.MESSAGE_ERROR, MyMassageMessage.MESSAGE_ACCESS_DENIED, FacesMessage.SEVERITY_FATAL, null);
				FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.redirectToIndexFullUrl());
			} catch (IOException e) {
				CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.FATAL, LogTypeEnum.APPLICATION, e, MyMassageProperties.PARAM_SYSTEM_USER);
			}
		}
		this.logsList = this.logEJB.getAllLogsByDate();
		this.logsList.size();
	}
	
}
