package edu.jcu.rbac;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.StopWatch;

import edu.jcu.rbac.combinations.SubsetHelper;
import edu.jcu.rbac.common.Utils;
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
	public void extractRoles(){

		final StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		
		int i = 1;

		/** Starting calculation **/
		LOGGER.info("Extracting all roles");
		LOGGER.info("Starting long calculations: " + stopwatch);
		
		SubsetHelper subsetHelper = new SubsetHelper();

		for(Sequence sequence : sequences){
			
			stopwatch.split();
			LOGGER.info("Extracting sequence " + i + "/" + sequences.size() + " " + stopwatch); 
			i++;
			// vyber všechna oprávnění větší než k
			if(sequence.size() < Utils.getConfig().getMinRoleSize()) {
				//addRestPermissions(permissionSet, userUniquePermissionList.get(permissionSet));
				continue;
			}
			// zpracovani opravneni
			subsetHelper.setInput(sequence.getPermissions());
			//combinations.setInput(sequence.getPermissions());
			
			// hlavni limit
			Integer maxLimit = Math.min(sequence.size(), Utils.getConfig().getMaxRoleSize());
			
			LOGGER.info("Current sequence size: " + sequence.size()  + " maxRoleSize = " + maxLimit );
			
			int n = 1, m = maxLimit - Utils.getConfig().getMinRoleSize();
			for(int roleMaxSize = Utils.getConfig().getMinRoleSize(); roleMaxSize <= maxLimit; roleMaxSize++){
				
				// tvoříme skupiny pro konkrétní i
				LOGGER.info("Groups of size " + roleMaxSize + ":" + sequence.size());
				subsetHelper.setK(roleMaxSize);
				subsetHelper.processSubsets();
				
				List<List<Permission>> res = subsetHelper.getSubsets();
				//List<List<Permission>> res = combinations.generateCombinations(roleMaxSize);
				
				LOGGER.info("Extracting subset " +  n + "/" + m + " done" + " " + res.size());
				
				// procházení výsledků setů a tvorba rolí
				for(List<Permission> sub: res){
					
					Role role = new Role();
					role.setPermissions(sub);
					role.calculateUsersFromPermissions();
					
					if(!candidateRoles.contains(role))
						candidateRoles.add(role);
					
				}	
				n++;
			}
			
		}
			
		
		LOGGER.info("Candidate role list size:" + candidateRoles.size());
		LOGGER.info("Finished calculating " + stopwatch);
		
	}
	public List<Role> getCandidateRoles() {
		return candidateRoles;
	}
}

