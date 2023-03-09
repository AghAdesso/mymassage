package beans;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@SuppressWarnings("serial")
@ManagedBean(name = "navigation")
@SessionScoped
public class NavigationBean implements Serializable {
	
	public void addMessage(String summary, String detail, Severity severity, String exception) {
		FacesContext context = FacesContext.getCurrentInstance();
	    context.getExternalContext().getFlash().setKeepMessages(true);
	    context.addMessage(null, new FacesMessage(severity, summary, detail));
	}

	public String redirectToIndex() {
		return "/index.xhtml?faces-redirect=true";
	}
	
	public String redirectToIndexFullUrl() {
		return "/mymassage/index.xhtml?faces-redirect=true";
	}

	public String toLogin() {
		return "/login/login.xhtml";
	}

}