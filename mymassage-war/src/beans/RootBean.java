package beans;

import java.util.ResourceBundle;

import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import lombok.Data;

@Data
public class RootBean {
	protected ResourceBundle bundleConfig;
	protected ResourceBundle bundleLabels;
	protected ResourceBundle bundleMessages;

	@ManagedProperty(value = "#{navigation}")
	protected NavigationBean navigation;

	public RootBean() {
		FacesContext context = FacesContext.getCurrentInstance();
		bundleConfig = context.getApplication().getResourceBundle(context, "config");
		bundleLabels = context.getApplication().getResourceBundle(context, "labels");
		bundleMessages = context.getApplication().getResourceBundle(context, "messages");
	}
}
