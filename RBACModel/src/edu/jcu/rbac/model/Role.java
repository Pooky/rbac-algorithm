package edu.jcu.rbac.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstrace nově vytvořené role
 * @author martin.klima
 *
 */
public class Role {

	Set<Permission> permissions = new HashSet<Permission>();
	List<User> users = new ArrayList<User>();
	List<Role> childrens = new ArrayList<Role>();
	Role parent;
	
	public Role(){
		
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}
	
	public int getPermissionsCount(){
		return permissions.size();
	}
    @Override
	public boolean equals(Object obj){
		
		// porovnání rolí
		Role role = (Role) obj;
		if(role.getPermissions().equals(this.getPermissions())){
			return true;
		}
		return false;
		
	}
	/**
	 * Porovná daná role, jestli se jedná o roli podřazenou nebo nadřazenou
	 * @param possibleChildRole
	 * @return
	 */
	public boolean isParent(Role possibleChildRole){
	
		// stejná role
		if(this.equals(possibleChildRole) || this == possibleChildRole){
			return false;
		}
		
		// pokud tato role vlastní všechna oprávnění a ještě nějaké navíc
		if(getPermissions().containsAll(possibleChildRole.getPermissions())){
			return true;
		}else{
			return false;
		}
		
	}
	public boolean isChild(Role possibleParentRole){
		
		// stejná role
		if(this.equals(possibleParentRole) || this == possibleParentRole){
			return false;
		}
		
		// pokud tato možná role vlastní všechna oprávnění
		if(possibleParentRole.getPermissions().containsAll(getPermissions())){
			return true;
		}else{
			return false;
		}
	
	}
	
	public String toString(){
		
		StringBuilder output = new StringBuilder();
		output.append("Users: ");
		output.append(users.size());
		output.append(" = ");
		output.append("[ ");
		for(Permission permission : getPermissions()){
			output.append(permission.getName().replaceAll(" ", "_"));
			output.append(",");
		}
		output.append(" ]");
		
		return output.toString();
		
	}

	public Role addPermission(Permission permission) {
		this.permissions.add(permission);
		return this;
	}
	
	public int hashCode(){
		return permissions.hashCode();
	}

	public Role addUser(User user) {
		if(!getUsers().contains(user)){
			this.users.add(user);
		}
		return this;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void addChild(Role role) {
		childrens.add(role);
	}
	public void setParent(Role role){
		this.parent = role;
	}
	/**
	 * Add unique users
	 * @param users
	 */
	public void addUsers(List<User> users) {
		
		for(User user: users){
			if(!getUsers().contains(user)){
				addUser(user);
			}
		}
	}
	/**
	 * Obsahuje aktuální role s oprávněními některé z těchto oprávnění
	 * @param permissionsToTest
	 * @return
	 */
	public boolean containsAnyOfThisPermissions(Set<Permission> permissionsToTest) {
		
		for(Permission permission : getPermissions()){
			if(permissionsToTest.contains(permission)){
				return true;
			}
		}
		return false;
		
	}
	public boolean containsAllUsers(List<User> usersToTest){
		if(getNumberOfDifferendUsers(usersToTest) == 0){
			return true;
		}else{
			return false;
		}
	}
	
	public int getNumberOfDifferendUsers(List<User> usersToTest){
		return users.size() -getNumberOfCommonUsers(usersToTest);
	}

	public int getNumberOfCommonUsers(List<User> usersToTest) {
		
		int numberOfCommonUsers = 0;
		for(User user : this.users){
			if(usersToTest.contains(user)){
				numberOfCommonUsers++;
			}
		}
		
		return numberOfCommonUsers;
	}
	
	
	public String getUsersAsString(){
		StringBuilder output = new StringBuilder();
		output.append("USERS: ");
		for(User user: getUsers()){
			output.append(user.getName());
			output.append(",");
		}
		output.deleteCharAt(output.length()-1);
		return output.toString();
	}
	
}
