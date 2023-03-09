package beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import ejb.ParameterEJBRemote;
import lombok.Getter;
import lombok.Setter;
import model.LogSeverityEnum;
import model.LogTypeEnum;
import model.Parameter;
import model.ParameterTypeEnum;
import util.CustomLogger;
import util.MyMassageMessage;
import util.MyMassageProperties;

@ManagedBean(name = "parameterBean")
@ViewScoped
@Getter
@Setter
public class ParameterBean extends RootBean {

	private List<Parameter> parametersList;
	private Parameter selectedParameter;
	private List<SelectItem> parameterTypeList;
	
	@EJB
	private ParameterEJBRemote parameterEJB;
	
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;	
	
	public ParameterBean() {
		this.parametersList = null;
		this.selectedParameter = null;
		this.parameterTypeList = new ArrayList<SelectItem>();
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
		
		this.initParameterList();
	}
	
	public void initParameterList() {
		this.parameterTypeList.clear();
		this.parameterTypeList.add(new SelectItem(ParameterTypeEnum.GES.toString(), ParameterTypeEnum.GES.toString()));
		if(loginBean.getRole().equals(MyMassageProperties.ROLE_GESTIONNAIRE)) {
			this.parametersList = this.parameterEJB.getGESParameter();
		} else {
			this.parametersList = this.parameterEJB.getAllParameters();
			this.parameterTypeList.add(new SelectItem(ParameterTypeEnum.ADM.toString(), ParameterTypeEnum.ADM.toString()));
		}
	}
	
	public void saveParameter() throws Exception {
				
		this.selectedParameter = this.parameterEJB.saveParameter(this.selectedParameter);
		
		this.initParameterList();

	}
	
	public void cancelParameter() {
		this.initParameterList();
	}
	
}
