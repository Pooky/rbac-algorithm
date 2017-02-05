package edu.jcu.rbac.elements;

import java.util.ArrayList;
import java.util.List;

import edu.jcu.rbac.common.Parameters;
import edu.jcu.rbac.common.Utils;

public class Sequence {

	private List<Permission> permissions;
	private List<User> users;
	
	public Sequence(){
		this.permissions = new ArrayList<Permission>();
	}
	
	public void addPermission(Permission permission){
		if(!permissions.contains(permission)){
			permissions.add(permission);
		}	
	}
	/**
	 * Je sequence validní?
	 * @return
	 */
	public boolean isValid(){
		
		boolean isValid = true;
		
		for(Permission permission: permissions){
			
			List<User> usersToCompare = permission.getUsers();
			// iterace pres list a porovnani
			for(Permission secondPerm: permissions){
				
				if(permission.equals(secondPerm))
					continue;
				
				// stejné hodnoty v obou listech
				List<User> common = new ArrayList<User>(usersToCompare);
				common.retainAll(secondPerm.getUsers());
				
				// musí mít stejné elementy
				if(common.size() < Parameters.minUsersForRole.getValue()){
					isValid = false;
					break;
				}
				
			}
		
			if(!isValid){
				break;
			}
		
		}
		
		return isValid;
	}

	public void removePermission(Permission secondPerm) {
		this.permissions.remove(secondPerm);
	}

	public int size() {
		return this.permissions.size();
	}

	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("Sequence: ");
		builder.append(Utils.toString(permissions));
		
		return builder.toString();

	}

	public List<Permission> getPermissions() {
		return permissions;
	}
	
	public void addUser(User user){
		if(!users.contains(user)){
			users.add(user);
		}
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
}