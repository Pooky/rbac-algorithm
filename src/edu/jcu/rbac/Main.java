package edu.jcu.rbac;


import org.apache.commons.cli.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.jcu.rbac.common.Config;
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
		
		// create Options object
		Options options = new Options();
		
        Option inputDataSet = new Option("c", "config", true, "config file path");
        inputDataSet.setRequired(true);
        options.addOption(inputDataSet);
		

        HelpFormatter formatter = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();
        CommandLine line;
        
        try {
            // parse the command line arguments
            line = parser.parse(options, args );
        }catch( ParseException exp ) {
            // oops, something went wrong
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
            formatter.printHelp("RBAC model algorith", options);
            System.exit(1);
            return;
        }
		
        Config config = new Config(line.getOptionValue("c"));
        Utils.setConfig(config);
        
		List<RestPermission> restPermissions = new ArrayList<RestPermission>();
		Source source = new Source();
		Cleaner cleaner = new Cleaner(restPermissions);
		
		source.initDataSet(config.getDataSetPath());
		//source.initDataSet6(); // permissions 256:users:39
		
		RulesManager rulesManager = new RulesManager(cleaner, source);
		
		rulesManager.removePermissionWhichCantMakeRole(); // removed 214 
		List<Sequence> list = rulesManager.createSequences(); // sequence ok

		
		/**
		 * Problém je v rest permission, je tam fůra blbostí i pro uživatele, který takové permission nemá
		 * Opraveno
		 */
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
