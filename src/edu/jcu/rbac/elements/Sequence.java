package edu.jcu.rbac.elements;

import java.util.ArrayList;
import java.util.List;

import edu.jcu.rbac.common.Utils;

public class Sequence {

	private List<Permission> permissions;
	private List<User> users;
	
	public Sequence(List<Permission> list){
		this.permissions = list;
	}
	
	public Sequence(){
		this.permissions = new ArrayList<Permission>();
	}
	
	public boolean addPermission(Permission permission){
		if(!permissions.contains(permission)){
			permissions.add(permission);
			return true;
		}	
		return false;
	}
	/**
	 * Je sequence validní?
	 * @return
	 */
	public boolean isValid(){
		
		boolean isValid = true;
		
		for(Permission permission: permissions){
			
			List<User> commonUsers = new ArrayList<>(permission.getUsers());
			// iterace pres list a porovnani
			for(Permission secondPerm: permissions){
				
				if(permission.equals(secondPerm))
					continue;
				
				// stejné hodnoty v obou listech
				commonUsers.retainAll(secondPerm.getUsers());
				
				// musí mít stejné elementy
				if(commonUsers.size() < Utils.getConfig().getMinUsersForRole()){
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

	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof Sequence == false){
			return false;
		}

		List<Permission> seqPerm = ((Sequence) obj).getPermissions();
		
		if(seqPerm.size() == getPermissions().size() && getPermissions().containsAll(seqPerm)){
			return true;
		}
		return false;

	}
}
	
	
	
