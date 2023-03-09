package ejb;

import java.util.List;

import javax.ejb.Remote;

import model.Parameter;

@Remote
public interface ParameterEJBRemote {
	public List<Parameter> getGESParameter();
	public List<Parameter> getAllParameters();
	public Parameter saveParameter(Parameter parameter);
	public Parameter getParameterByKey(String key);
}
