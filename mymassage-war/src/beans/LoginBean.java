package beans;

import java.io.IOException;
import java.net.URI;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import ejb.ParameterEJBRemote;
import ejb.RoleEJBRemote;
import ejb.UserEJBRemote;
import lombok.Getter;
import lombok.Setter;
import loro.ADUser;
import loro.ActiveDirectoryParameters;
import loro.LoroFoundation;
import microsoft.exchange.webservices.data.autodiscover.IAutodiscoverRedirectionUrl;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import model.LogSeverityEnum;
import model.LogTypeEnum;
import model.User;
import util.CustomLogger;
import util.MyMassageMessage;
import util.MyMassageProperties;

@ManagedBean(name = "loginBean")
@SessionScoped
@Getter
@Setter
public class LoginBean extends RootBean {

	private String username;
	private String password;
	private Boolean isLogged;
	private User user;
	private String role;
	private ADUser adUser;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private ExchangeService service;
	
	@EJB
	private UserEJBRemote loginEJB;
	@EJB
	private ParameterEJBRemote paramEJB;
	@EJB
	private RoleEJBRemote roleEJB;

	public LoginBean() {
		
	}
	
	@PostConstruct
	public void init() {
		
		// check if user is already logged in
		if (this.isLogged != null) {
			if(this.isLogged == true) {			
				try {
					FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.redirectToIndexFullUrl());
				} catch (IOException e) {
					CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.FATAL, LogTypeEnum.APPLICATION, e, null);
				}
			}
		} else {
			this.postInit();
		}

	}
	
	public void keepSessionAlive(ActionEvent event) {
	}

	private void postInit() {		
		this.isLogged = false;
		this.role = "";
		this.adUser = null;
		this.user = null;
		this.username = "";
		this.password = "";
		this.firstName = "";
		this.lastName="";
		this.phoneNumber = "";
		try {
            //service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
            //service.setUrl(new URI("https://mail.loro.ch/ews/Exchange.asmx"));
            //service.setUrl(new URI("https://outlook.office365.com/EWS/exchange.asmx"));
            
        } catch (Exception e) {
			CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e.toString() , MyMassageProperties.PARAM_SYSTEM_USER);
        }
	}
	
	public static class RedirectionUrlCallback implements IAutodiscoverRedirectionUrl {
        public boolean autodiscoverRedirectionUrlValidationCallback(String redirectionUrl) {
          return redirectionUrl.toLowerCase().startsWith("https://");
        }
    }

	
	public String doLogin() {

		this.user = null;
		this.isLogged = false;
		this.role = "";
		this.adUser = null;
		
		// AD connection
		// ActiveDirectoryParameters activeDirectoryParameters = new ActiveDirectoryParameters();
		// activeDirectoryParameters.setDn(bundleConfig.getString("ad.dc"));
		// activeDirectoryParameters.setDomain(bundleConfig.getString("ad.domain"));
		// activeDirectoryParameters.setUrl(bundleConfig.getString("ad.url"));
		try {
			// check if the user / pwd is right
			this.adUser = new ADUser();// LoroFoundation.getADUser(activeDirectoryParameters, this.username, this.password);
			// if the user has been found in AD goes on else go on catch
			this.adUser.setFirstName("anas");
			this.adUser.setLastName("GHANAM");
			this.adUser.setLogin("yvco1");
			this.adUser.setMail("anas.ghanam@adesso.ch");
			this.adUser.setPhone("0033644227155");

			this.isLogged = true;
			this.firstName = this.adUser.getFirstName();
			this.lastName = this.adUser.getLastName();
			this.phoneNumber = this.adUser.getPhone();
			// looks in the local DB if the user has access to the application
			User user = loginEJB.getUserByLogin(this.adUser.getLogin());
			// the user exists in the DB
			if (user != null && user.getIsActive()) {	

				this.user = user;
				this.role = user.getRole().getCode();
				
				navigation.addMessage(MyMassageMessage.MESSAGE_SUCCESS, MyMassageMessage.MESSAGE_LOGIN_SUCCESS, FacesMessage.SEVERITY_INFO, null);
				CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.INFO, LogTypeEnum.CONNECTION, "Authentification réussie", this.user.getLogin());
				
			} else {
				// the user does not exist in the DB, it means (s)he has the collaborateur's role
				
				this.role = roleEJB.getRoleByCode(MyMassageProperties.ROLE_COLLABORATEUR).getCode();
				navigation.addMessage(MyMassageMessage.MESSAGE_SUCCESS, MyMassageMessage.MESSAGE_LOGIN_SUCCESS, FacesMessage.SEVERITY_INFO, null);
				CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.INFO, LogTypeEnum.CONNECTION, "Authentification réussie", this.username);
			}
			// before redirecting, bind the credentials for the Exchange API
			
			//https://learn.microsoft.com/en-us/exchange/clients-and-mobile-in-exchange-online/deprecation-of-basic-authentication-exchange-online
			//on ne peut plus s'authentifier user password depuis fin 2022
			//il faut utiliser graphapi office 365 mais il faut enregistrer l'application dans le portail
			//azure avec "azure active directory"
//			String email = "yves.collet@loro.ch.onmicrosoft.com";
//			service = new ExchangeService(ExchangeVersion.Exchange2013);
//			service.setUrl(new URI("https://outlook.office365.com/EWS/exchange.asmx"));
//			service.setCredentials(new WebCredentials(email, password));
//            service.autodiscoverUrl(email, new RedirectionUrlCallback());
//            service.setTraceEnabled(true);
			this.password = "";
			return navigation.redirectToIndex();		
		} catch (Exception e) {
			// connection problem to AD
			this.init();
			navigation.addMessage(MyMassageMessage.MESSAGE_ERROR, MyMassageMessage.MESSAGE_LOGIN_ERROR, FacesMessage.SEVERITY_FATAL, e.toString());
			CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.FATAL, LogTypeEnum.APPLICATION, e, null);
			return navigation.toLogin();
		}

	}
	
	public void doLogout() {
		this.isLogged = false;
		this.role = "";
		this.adUser = null;
		this.username = "";
		this.password = "";
		this.firstName = "";
		this.lastName = "";
		this.phoneNumber = "";
		try {
			// on logout, invalidates the session ands redirects to the search/login page
			navigation.addMessage(MyMassageMessage.MESSAGE_SUCCESS, MyMassageMessage.MESSAGE_LOGOUT_SUCCESS, FacesMessage.SEVERITY_INFO, null);
			
			FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
			this.postInit();
			FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.redirectToIndexFullUrl());
		} catch (IOException e) {
			CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.FATAL, LogTypeEnum.APPLICATION, e, null);
		}
	}
}
