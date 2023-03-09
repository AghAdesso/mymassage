package ejb;

import java.util.List;

import javax.ejb.Remote;

import model.Log;

@Remote
public interface LogEJBRemote {
	public List<Log> getAllLogsByDate();
}
