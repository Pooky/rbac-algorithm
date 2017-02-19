package edu.jcu.rbac;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import edu.jcu.rbac.common.Parameters;
import edu.jcu.rbac.common.Utils;
import edu.jcu.rbac.elements.Permission;
import edu.jcu.rbac.elements.RestPermission;
import edu.jcu.rbac.elements.Sequence;
import edu.jcu.rbac.elements.User;

public class Rules {
	
	private static final Logger LOGGER = Logger.getLogger(Rules.class.getName() );
	/**
	 * Odebrání oprávnění, které nemohou tvořit role
	 * @return
	 */
	public static List<RestPermission> removePermissionWhichCantMakeRole(Source input){
		
		List<RestPermission> restPermissions = new ArrayList<RestPermission>();
		
		int removedPermissions = 0;
		
		LOGGER.info("Permission count:" + input.getPermissions().size());
		
		Iterator<Permission> itr = input.getPermissions().iterator();
		while(itr.hasNext()){
			Permission permission = itr.next();
			if(permission.getUsers().size() < Parameters.minUsersForRole.getValue()){
				
				RestPermission restPerm = new RestPermission(permission);
				for(User user: input.getUsers()){
					restPerm.addUser(user);
				}
				restPermissions.add(restPerm);
				// odebereme to oprávnění
				itr.remove();
				removedPermissions++;
			}
		}
		
		//LOGGER.info("Removed permissions:" + removedPermissions);
		LOGGER.info("Removed permissions: " + Utils.toString(restPermissions));
		
		
		return restPermissions;
		
	}
	
	/**
	 * Vytvoření sequnce, které mohou být kandidáty na role
	 * @param input
	 * @return 
	 */
	public static List<Sequence> createSequences(Source input){
		
		List<Sequence> resultSequencesList = new ArrayList<Sequence>();
		List<Permission> testedPermissions = new ArrayList<Permission>();
		
		Iterator<Permission> permIterator = input.getPermissions().iterator();
		while(permIterator.hasNext()){
			
			Permission firstPerm = permIterator.next();	
			/**
			 * Pro všechny uživatele, kteří vlastní toto oprávnění
			 */
			for(User user : firstPerm.getUsers()){
				
				// sequence
				Sequence sequence = new Sequence();
				// první prvek
				sequence.addPermission(firstPerm);
				/**
				 * Pro všechny oprávnění, které má tento uživatel
				 */
				for(Permission secondPerm :  user.getPermissions()){
					if(secondPerm.equals(firstPerm)){
						//LOGGER.info("skiping same permission");
						continue;
					}
					
					boolean sequenceChanged = sequence.addPermission(secondPerm);
					
					if(sequenceChanged){
						// check sequnce, splňuje pravidla?
						if(!sequence.isValid()){
							// odebereme poslední prvek
							sequence.removePermission(secondPerm);
						}
					}
				}	
				
				/**
				 * Prošli jsme všechny oprávnění, je seqkvence validní?
				 */

				// zkontrolujeme velikost
				if(sequence.size() >= Parameters.minRoleSize.getValue()){
					if(!resultSequencesList.contains(sequence))
						resultSequencesList.add(sequence);
				}
				
			}
		}
		return resultSequencesList;
		
	}
	
}
