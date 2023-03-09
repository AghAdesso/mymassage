package validator;

import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import loro.ADUser;
import loro.ActiveDirectoryParameters;
import loro.LoroFoundation;

@FacesValidator(value = "UserADValidator")
public class UserADValidator implements Validator {
	
	private ResourceBundle bundleConfig;

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2) throws ValidatorException {
		FacesContext context = FacesContext.getCurrentInstance();
		bundleConfig = context.getApplication().getResourceBundle(context, "config");
		ActiveDirectoryParameters activeDirectoryParameters = new ActiveDirectoryParameters();
		activeDirectoryParameters.setDn(bundleConfig.getString("ad.dc"));
		activeDirectoryParameters.setDomain(bundleConfig.getString("ad.domain"));
		activeDirectoryParameters.setUrl(bundleConfig.getString("ad.url"));
		ADUser adUser = null;
		try {
			adUser = LoroFoundation.getADUser(activeDirectoryParameters, bundleConfig.getString("userAD.login"), bundleConfig.getString("userAD.password"), arg2.toString());
			if (adUser == null){
				throw new ValidatorException(new FacesMessage("L'utilisateur '" + arg2.toString() + "' n'existe pas."));
			}	
		} catch (Exception e) {
			throw new ValidatorException(new FacesMessage(e.toString()));
		}
	}

}
