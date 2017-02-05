package edu.jcu.rbac;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import edu.jcu.rbac.common.CSVUtils;
import edu.jcu.rbac.elements.Role;
import edu.jcu.rbac.elements.User;

public class Output {
	
	private List<Role> roles;
	
	public Output(){
		
	}
	
	public void exportToFile() throws IOException{
    
		String permission_role = "/data/output/permission_role.csv";
		String user_role = "/data/output/user_role.csv";
		String user_permission = "/data/output/user_permission.csv";
		
        FileWriter writerPermissionRole = new FileWriter(permission_role);
        FileWriter writerUserRole = new FileWriter(user_role);
        FileWriter writerUserPermission = new FileWriter(user_permission);
                      
        // zapíšeme do permission role
        for(Role role : roles){
        	for(perm : role.getPermissions()){
        		CSVUtils.writeLine(writerPermissionRole, Arrays.asList(user.getIdentifier(), ));
        	}
        	
        }
        
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

}
