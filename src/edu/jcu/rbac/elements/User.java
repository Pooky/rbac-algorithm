package edu.jcu.rbac.elements;

import java.util.ArrayList;
import java.util.List;

import edu.jcu.rbac.common.IElement;

public class User implements IElement {

	private String name;
	private String id;
	private List<Permission> permissions = new ArrayList<Permission>();
	
	public User(String name, String userId){
		this.setName(name);
		this.id = userId;
	}
	
	public User() {
		// TODO Auto-generated constructor stub
	}

	public User(String string) {
		this.setName(string);
	}

	public User setPermission(Permission permission){
		permissions.add(permission);
		
		return this;
	}
	
	public List<Permission> getPermissions(){
		return permissions;
	}

	public User addRole(List<Permission> role) {
		permissions.addAll(role);
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User addRole(Role role) {
		this.permissions.addAll(role.getPermissions());
		return this;
	}

	public User addPermission(Permission permission) {
		this.permissions.add(permission);
		return this;
	}

	public String getIdentifier() {
		return this.id.toString();
	}
	
	
}
