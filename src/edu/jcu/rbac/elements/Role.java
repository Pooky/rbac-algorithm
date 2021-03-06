package edu.jcu.rbac.elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.jcu.rbac.common.IElement;

/**
 * Abstrace nově vytvořené role
 * @author martin.klima
 *
 */
public class Role implements IElement {

	List<Permission> permissions = new ArrayList<Permission>();
	List<User> users = new ArrayList<User>();
	List<Role> childrens = new ArrayList<Role>();
	Role parent;
	
	private String name;
	private Integer id;
	
	public Role(){
		
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> sub) {
		this.permissions = sub;
	}
	
	public String getIdentifier() {
		return this.id.toString();
	}
	
	public int getPermissionsCount(){
		return permissions.size();
	}
    @Override
	public boolean equals(Object obj){
		
		// porovnání rolí
		Role role = (Role) obj;
		
		if(role.getPermissions().containsAll(this.getPermissions()) && this.getPermissions().containsAll(role.getPermissions())){
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
	/**
	 * Naplnění uživatelů z daného oprávnění
	 */
	public void calculateUsersFromPermissions(){
		
		for(Permission perm : permissions){
			for(User user : perm.getUsers()){
				this.addUser(user);
			}
		}
		for(Permission perm : permissions){
			users.retainAll(perm.getUsers());
		}
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
	 * @param list
	 * @return
	 */
	public boolean containsAnyOfThisPermissions(List<Permission> list) {
		
		for(Permission permission : getPermissions()){
			if(list.contains(permission)){
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

	public void setId(int i) {
		this.id = i;
	}
	
}
