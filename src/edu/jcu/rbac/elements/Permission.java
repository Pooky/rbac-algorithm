package edu.jcu.rbac.elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Konkrétní abstrakce oprávnění
 * @author martin.klima
 *
 */
public class Permission {
	
	private String name;
	private Integer id;
	private List<User> users; // uživatelé, kteří mají toto oprávnění

	public Permission(String name, Integer permId){
		this(name);
		this.setId(permId);
	}

	public Permission(String string) {
		this.setName(string);
		this.users = new ArrayList<User>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer permId) {
		this.id = permId;
	}

	public String getIdentifier() {
		return this.name;
	}

	public void addUser(User user) {
		if(!users.contains(user))
			users.add(user);
	}
	
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
}
