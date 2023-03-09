package ejb;

import java.util.List;

import javax.ejb.Remote;

import model.Role;

@Remote
public interface RoleEJBRemote {
	public Role getRoleByCode(String code);
	public List<Role> getAllRoles();
}
