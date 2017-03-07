package edu.jcu.rbac;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import edu.jcu.rbac.common.IElement;
import edu.jcu.rbac.elements.Permission;
import edu.jcu.rbac.elements.RestPermission;
import edu.jcu.rbac.elements.Role;
import edu.jcu.rbac.elements.Sequence;
import edu.jcu.rbac.elements.User;

public class Cleaner {

	Logger logger  = Logger.getLogger(Main.class.getName());
	
	private List<RestPermission> restPermissions;

	public Cleaner(List<RestPermission> restPermissionsList){
		this.restPermissions = restPermissionsList;
	}
	
	
	/**
	 * Očištění zbytkových oprávánění od duplicit definovaných v rolích
	 */
	public void cleanRestPermissions(List<Role> roles){
	
		for(Role role : roles){
			cleanRestPermission(role.getPermissions(), role.getUsers());
		}
		
	}
	/**
	 * Očištění oprávnění a uživatelů
	 * Ty které už jsou v rolích, tak odebereme ze zbytkových oprávnění
	 * @param list
	 * @param users
	 */
	private void cleanRestPermission(List<Permission> list, List<User> users) {
		
		Iterator<RestPermission> itr = restPermissions.iterator();
		while(itr.hasNext()){
			RestPermission rest = itr.next();
			for(Permission perm : list){
				if(rest.getPermission().equals(perm)){
					rest.removeAllUsers(users);
				}
			}
			// nemáme už žádné uživatele, odebereme oprávnění
			if(rest.getUsers().size() == 0)
				itr.remove();
		}
		
	}
	/**
	 * Pokud některé oprávnění nemáme v seqkvencích,
	 * musíme ho zařadit do zbytkov
	 * @param list
	 * @param allPermissions
	 */
	public void calculateRestPermissionsFromSequences(List<Sequence> list, List<Permission> allPermissions){
		
		Set<Permission> uniquePermissions = new HashSet<Permission>();
		for(Sequence seq : list){
			uniquePermissions.addAll(seq.getPermissions());
		}
		
		logger.info("unique perm:" + uniquePermissions.size());
		logger.info("unique all perm:" + allPermissions.size());
		logger.info("restPermissions size perm:" + restPermissions.size());
		
		// unique permissions z sekvenci
		List<Permission> permissionsToBeSaved = new ArrayList<Permission>(allPermissions);
		permissionsToBeSaved.removeAll(uniquePermissions);
		// uložíme do rest persmisisons
		for(Permission perm : permissionsToBeSaved){
			addRestPermission(perm, perm.getUsers());
		}	
		
		
		logger.info("restPermissions size perm:" + restPermissions.size());
		logger.info("unique all perm:" + allPermissions.size());
		
	}
	/**
	 * Přidání oprávnění do zbytkových a přiřazení jich k uživateli
	 * @param permissions
	 * @param user
	 */
	public void addRestPermissions(List<Permission> permissions, User user) {
		for(Permission permission : permissions){
			addRestPermission(permission, user);
		}
	}
	
	public void addRestPermission(Permission permission, User user){
		
		RestPermission restPermission = new RestPermission(permission, user);
		
		if(restPermissions.contains(restPermission)){
			RestPermission x = restPermissions.get(restPermissions.indexOf(restPermission));
			if(!x.getUsers().contains(user))
				x.addUser(user);
		}else{
			restPermissions.add(restPermission);
		}
		
	}
	public void addRestPermissionsFromRole(Role role) {
		
		for(Permission permission : role.getPermissions()){
			addRestPermission(permission, role.getUsers());
		}
		
	}
	/**
	 * Přidáme všechny uživatele k jednomu oprávnění
	 * @param permission
	 * @param users
	 */
	public void addRestPermission(Permission permission, List<User> users) {
			
		for(User user: users){
			addRestPermission(permission, user);
		}
		
	}


	public  List<RestPermission> getRestPermissions() {
		return this.restPermissions;
	}


}

