package edu.jcu.rbac.model;

import java.util.ArrayList;
import java.util.List;

public class User {

	private String name;
	private Long id;
	private List<Permission> permissions = new ArrayList<Permission>();
	
	public User(String name, Long id){
		this.setName(name);
		this.id = id;
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
	
	
}
