package edu.jcu.rbac;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import edu.jcu.rbac.common.CSVUtils;
import edu.jcu.rbac.elements.Permission;
import edu.jcu.rbac.elements.RestPermission;
import edu.jcu.rbac.elements.Role;
import edu.jcu.rbac.elements.User;

public class Output {

	private static final Logger LOGGER = Logger.getLogger( Output.class.getName() );
	
	private List<Role> roles;
	private List<RestPermission> restPermissions;
	
	public Output(List<Role> roles, List<RestPermission> restPermissions){
		this.roles = roles;
		this.restPermissions = restPermissions;
		
		LOGGER.info("Inicializing output");
	}
	
	public void exportToFile() throws IOException{
    
		String permission_role = "data/output/permission_role.csv";
		String user_role = "data/output/user_role.csv";
		String user_permission = "data/output/user_permission.csv";
		
        FileWriter writerPermissionRole = new FileWriter(permission_role);
        FileWriter writerUserRole = new FileWriter(user_role);
        FileWriter writerUserPermission = new FileWriter(user_permission);
       
        
        
        LOGGER.info("Exporting role");
        // zapíšeme do permission role
        for(Role role : roles){
        	for(Permission perm : role.getPermissions()){
        		CSVUtils.writeLine(writerPermissionRole, Arrays.asList(role.getIdentifier(), perm.getIdentifier()));
        	}
        	
        }
        
        LOGGER.info("Exporting user_role.csv");
        // zapíšeme do user_role
        for(Role role : roles){
        	for(User user : role.getUsers()){
        		CSVUtils.writeLine(writerUserRole, Arrays.asList(role.getIdentifier(), user.getIdentifier()));
        	}
        	
        }
        
        // zapíšeme zbytková oprávnění
        for(RestPermission perm: restPermissions){
        	for(User user : perm.getUsers()){
        		CSVUtils.writeLine(writerUserPermission, Arrays.asList(user.getIdentifier(), perm.getIdentifier()));
        	}
        	
        }
        
        
        writerPermissionRole.flush();
        writerPermissionRole.close();
        
        writerUserPermission.flush();
        writerUserPermission.close();
        
        writerUserRole.flush();
        writerUserRole.close();
        
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

}
