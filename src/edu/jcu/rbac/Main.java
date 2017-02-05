package edu.jcu.rbac;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.jcu.rbac.common.Utils;
import edu.jcu.rbac.elements.Sequence;

public class Main {

	/**
	 * 
	 * @param args
	 */

	public static void main(String[] args) {
		
		Logger logger  = Logger.getLogger(Main.class.getName());

		
		Source source = new Source();
		
		source.initDataSet2();
		
		Rules.removePermissionWhichCantMakeRole(source);
		List<Sequence> list = Rules.createSequences(source);
		
		/*for(Sequence x : list){
			logger.info(x.toString());
		}*/
		
		RoleExtractor roleExtractor = new RoleExtractor(list);
		roleExtractor.extractRolesFromUser();
		
		//Utils.printRoles(roleExtractor.getCandidateRoles());
		Integer x = Utils.checkDuplicities(roleExtractor.getCandidateRoles());
		logger.info(x.toString());
		
		RoleSolver roleHiearchySolver = new RoleSolver(roleExtractor.getCandidateRoles());
		roleHiearchySolver.solveRolesParents();

		
		
		System.out.println("done");
		

		
	}

}
