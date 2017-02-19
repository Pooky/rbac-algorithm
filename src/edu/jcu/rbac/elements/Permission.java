package edu.jcu.rbac.elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.jcu.rbac.common.IElement;
import edu.jcu.rbac.common.Utils;

/**
 * Konkrétní abstrakce oprávnění
 * @author martin.klima
 *
 */
public class Permission implements IElement {
	
	private String name;
	private String id;
	private List<User> users; // uživatelé, kteří mají toto oprávnění

	public Permission(String name, String permId){
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

	public String getId() {
		return id;
	}

	public void setId(String permId) {
		this.id = permId;
	}

	public String getIdentifier() {
		return this.id.toString();
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
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(this.getIdentifier() + " : ");
		builder.append(Utils.toString(users));
		
		return builder.toString();

	}
}
