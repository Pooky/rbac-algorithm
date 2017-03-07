package edu.jcu.rbac.common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import edu.jcu.rbac.Main;
import edu.jcu.rbac.elements.Permission;
import edu.jcu.rbac.elements.RestPermission;
import edu.jcu.rbac.elements.Role;
import edu.jcu.rbac.elements.Sequence;

public class Utils {
	
	static Config config;
	static Logger logger  = Logger.getLogger(Main.class.getName());
	
	public static String toString(List<? extends IElement> permissions) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		
		for(IElement perm : permissions){
			builder.append(perm.getIdentifier());
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
	
	public static Config getConfig(){
		return config;
	}
	public static void setConfig(Config config){
		Utils.config = config;
	}
	
	
}
