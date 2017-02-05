package edu.jcu.rbac;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import edu.jcu.rbac.combinations.SubsetExample;
import edu.jcu.rbac.common.Parameters;
import edu.jcu.rbac.elements.Permission;
import edu.jcu.rbac.elements.Role;
import edu.jcu.rbac.elements.Sequence;

/**
 * Třída pro extrakci rolí
 */
public class RoleExtractor {

	private static final Logger LOGGER = Logger.getLogger( RoleSolver.class.getName() );
	private List<Sequence> sequences;
	private List<Role> candidateRoles;
	
	public RoleExtractor(){
		LOGGER.info("Starting RoleExtractor");
		this.candidateRoles = new ArrayList<Role>();
	}
	public RoleExtractor(List<Sequence> list) {
		this();
		this.sequences = list;
	}

	
	/**
	 * Extract all roles from set
	 */
	public void extractRolesFromUser(){
	
		LOGGER.info("Extracting all roles");
		
		SubsetExample subsetHelper = new SubsetExample();
		for(Sequence sequence : sequences){
			
			// vyber všechna oprávnění větší než k
			if(sequence.size() < Parameters.minRoleSize.getValue()) {
				//addRestPermissions(permissionSet, userUniquePermissionList.get(permissionSet));
				continue;
			}
			// zpracovani opravneni
			subsetHelper.setInput(sequence.getPermissions());

			// hlavni limit
			Integer maxLimit = Math.min(sequence.size(), Parameters.maxRoleSize.getValue());
			
			LOGGER.info("Current permission: " + sequence.size()  + " maxSize = " + maxLimit);
			
			for(int roleMaxSize = Parameters.minRoleSize.getValue(); roleMaxSize <= maxLimit; roleMaxSize++){
				
				// tvoříme skupiny pro konkrétní i
				System.out.println("Groups of size " + roleMaxSize + ":" + sequence.size());
				subsetHelper.setK(roleMaxSize);
				subsetHelper.processSubsets();
				
				LOGGER.info("Extracting subset : done" + " " + subsetHelper.getSubsets().size());
				
				// procházení výsledků setů a tvorba rolí
				for(Set<Permission> sub: subsetHelper.getSubsets()){
					
					Role role = new Role();
					role.setPermissions(sub);
					role.calculateUsersFromPermissions();
					
					if(!candidateRoles.contains(role))
						candidateRoles.add(role);
					
					
				}	
			}
		}
		
		LOGGER.info("Candidate role list size:" + candidateRoles.size());
	}
	public List<Role> getCandidateRoles() {
		return candidateRoles;
	}
}
