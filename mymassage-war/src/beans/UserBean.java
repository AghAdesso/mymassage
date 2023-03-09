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

import ejb.RoleEJBRemote;
import ejb.UserEJBRemote;
import lombok.Getter;
import lombok.Setter;
import loro.ADUser;
import loro.ActiveDirectoryParameters;
import loro.LoroFoundation;
import model.LogSeverityEnum;
import model.LogTypeEnum;
import model.Role;
import model.User;
import util.CustomLogger;
import util.MyMassageMessage;
import util.MyMassageProperties;


@ManagedBean(name = "userBean")
@ViewScoped
@Getter
@Setter
public class UserBean extends RootBean {

	private List<User> usersList;
	private User selectedUser;
	private List<Role> roles;
	private List<User> filteredUsers;
	
	@EJB
	private UserEJBRemote userEJB;
	@EJB
	private RoleEJBRemote roleEJB;
	
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	public UserBean() {
	}
	
	@PostConstruct
	public void init() {
		if (!loginBean.getRole().equals(MyMassageProperties.ROLE_ADMIN)) {
			try {
				navigation.addMessage(MyMassageMessage.MESSAGE_ERROR, MyMassageMessage.MESSAGE_ACCESS_DENIED, FacesMessage.SEVERITY_FATAL, null);
				FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.redirectToIndexFullUrl());
			} catch (IOException e) {
				CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.FATAL, LogTypeEnum.APPLICATION, e, MyMassageProperties.PARAM_SYSTEM_USER);
			}
		} 
		
		this.setRoles(roleEJB.getAllRoles());
		this.initUserLists();
	}
	
	public void initUserLists() {
		this.setUsersList(userEJB.getAllUsers());
	}
	
	public void cancelUser() {
		this.initUserLists();
	}
	
	public void saveUser() throws Exception {

		// if it's a new user
		// check if the user exists in AD and not in the DB
		if (this.selectedUser.getId() == null || this.selectedUser.getId() == 0) {
			FacesContext context = FacesContext.getCurrentInstance();
			bundleConfig = context.getApplication().getResourceBundle(context, "config");
			ActiveDirectoryParameters activeDirectoryParameters = new ActiveDirectoryParameters();
			activeDirectoryParameters.setDn(bundleConfig.getString("ad.dc"));
			activeDirectoryParameters.setDomain(bundleConfig.getString("ad.domain"));
			activeDirectoryParameters.setUrl(bundleConfig.getString("ad.url"));
			try {
				ADUser userAD = LoroFoundation.getADUser(activeDirectoryParameters, bundleConfig.getString("userAD.login"), bundleConfig.getString("userAD.password"),
						this.selectedUser.getLogin());
			} catch (Exception e) {
				throw new Exception("L'utilisateur '" + this.selectedUser.getLogin() + "' n'a pas été trouvé dans AD.");
			}
			if (userEJB.getUserByLogin(this.selectedUser.getLogin()) != null) {
				throw new Exception("L'utilisateur '" + this.selectedUser.getLogin() + "' existe déjà dans l'application.");
			}


		}
		
		Role role = roleEJB.getRoleByCode(this.selectedUser.getRole().getCode());
		this.selectedUser.setRole(role);

		this.selectedUser = this.userEJB.saveUser(this.selectedUser);
				
		this.initUserLists();
	}
	
	public void addUser() {
		Role role = roleEJB.getRoleByCode(MyMassageProperties.ROLE_COLLABORATEUR);
		this.selectedUser = new User(role);
		this.initUserLists();
	}
}
