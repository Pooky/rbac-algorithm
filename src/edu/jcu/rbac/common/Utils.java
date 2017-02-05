package edu.jcu.rbac.common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import edu.jcu.rbac.Main;
import edu.jcu.rbac.elements.Permission;
import edu.jcu.rbac.elements.Role;

public class Utils {
	
	
	static Logger logger  = Logger.getLogger(Main.class.getName());
	
	public static String toString(List<Permission> permissions) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		
		for(Permission perm : permissions){
			builder.append(perm.getName());
			builder.append(",");
		}
		builder.append("]");
		
		return builder.toString();
		
	}
	
	/**
	 * Vypsání listu rolí
	 * @param roles
	 */
	public static void printRoles(List<Role> roles){
	
		logger.info("Size: " + roles.size());
		for(Role role : roles){
			logger.info(role.toString());
			logger.info(role.getUsersAsString());

		}
		
	}
	
	public static Integer checkDuplicities(List<Role> roles){
		
		Set<Role> set = new HashSet<Role>(roles);

		if(set.size() < roles.size()){
		    return roles.size() - set.size();
		}
		return -1;
	}
	
}
