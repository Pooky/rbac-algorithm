package edu.jcu.rbac;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.jcu.rbac.common.Utils;
import edu.jcu.rbac.elements.Permission;
import edu.jcu.rbac.elements.RestPermission;
import edu.jcu.rbac.elements.Role;
import edu.jcu.rbac.elements.Sequence;
import edu.jcu.rbac.elements.User;

public class Main {
	/**
	 * 
	 * @param args
	 */

	public static void main(String[] args) {
		
		Logger logger  = Logger.getLogger(Main.class.getName());
		
		List<RestPermission> restPermissions = new ArrayList<RestPermission>();
		Source source = new Source();
		Cleaner cleaner = new Cleaner(restPermissions);
		
		source.initDataSet3(); // permissions 256:users:39
		
		restPermissions.addAll(Rules.removePermissionWhichCantMakeRole(source)); // removed 214 
		List<Sequence> list = Rules.createSequences(source); // sequence ok
		
		cleaner.calculateRestPermissionsFromSequences(list, source.getPermissions());
		
		RoleExtractor roleExtractor = new RoleExtractor(list);
		roleExtractor.extractRolesFromUser();
		
		//Utils.printRoles(roleExtractor.getCandidateRoles());
		Integer x = Utils.checkDuplicities(roleExtractor.getCandidateRoles());
		logger.info(x.toString());
		
		// vyřešení hiearchie
		RoleSolver roleHiearchySolver = new RoleSolver(roleExtractor.getCandidateRoles());
		roleHiearchySolver.setRestPermissions(restPermissions);
		List<Role> cleanedRoles = roleHiearchySolver.solveRolesParents();
		
		// validace výsledků
		
		// result
		Set<User> qUsers = new HashSet<User>();
		for(Role role : cleanedRoles){
			qUsers.addAll(role.getUsers());
		}
		System.out.println(qUsers.size());
		System.out.println(restPermissions.size());
		
		// uložení výsledku
		Output output = new Output(cleanedRoles, roleHiearchySolver.getRestPermissions());
		
		try {
			output.exportToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		System.out.println("done");
		

		
	}

}
