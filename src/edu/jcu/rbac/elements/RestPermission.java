package edu.jcu.rbac.elements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.jcu.rbac.common.IElement;

/**
 * Zbytkové oprávnění, ze kterého nelze vytvořit role
 * @author martin.klima
 *
 */
public class RestPermission implements IElement {

	protected Set<User> users;
	protected Permission permission;
	
	public RestPermission(Permission permission){
		this.permission = permission;
		this.users = new HashSet<User>();
	}

	public RestPermission(Permission permission, User user) {
		this(permission);
		this.addUser(user);
	}

	/**
	 * Přidání uživatele do zbytových uprávnění
	 * @param user
	 * @return
	 */
	public boolean addUser(User user){
		return this.users.add(user);
	}

	public Permission getPermission(){
		return this.permission;
	}
    @Override
    public boolean equals(Object object){
    	
    	RestPermission restPermission = (RestPermission)object;
    	if(this.getPermission().equals(restPermission.getPermission())){
    		return true;
    	}else{
    		return false;
    	}
    }
	
	public String toString(){
		
		StringBuilder result = new StringBuilder();
		result.append(permission.getName());
		result.append(" : ");
		for(User user: users){
			result.append(user.getName());
			result.append(",");
		}
		
		return result.toString();
	}

	public void removeAllUsers(List<User> users2) {
		this.users.removeAll(users2);
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	@Override
	public String getIdentifier() {
		return this.getPermission().getIdentifier();
	}
	
}
