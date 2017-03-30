package edu.jcu.rbac.elements;

import java.util.ArrayList;
import java.util.List;

import edu.jcu.rbac.common.IElement;

public class User implements IElement {

	private String name;
	private String id;
	private List<Permission> permissions = new ArrayList<Permission>();
	

	public User(String name) {
		this.setName(name);
		this.id = name;
	}	
	
	public User(String name, String userId){
		this.setName(name);
		this.id = userId;
	}
	
	public List<Permission> getPermissions(){
		return permissions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User addPermissions(List<Permission> permissions) {
		permissions.addAll(permissions);
		return this;
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
