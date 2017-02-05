package edu.jcu.rbac;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import edu.jcu.rbac.common.Parameters;
import edu.jcu.rbac.elements.Permission;
import edu.jcu.rbac.elements.Sequence;
import edu.jcu.rbac.elements.User;

public class Rules {
	
	private static final Logger LOGGER = Logger.getLogger(Rules.class.getName() );
	/**
	 * Odebrání oprávnění, které nemohou tvořit role
	 * @return
	 */
	public static int removePermissionWhichCantMakeRole(Source input){
		
		int removedPermissions = 0;
		
		LOGGER.info("Permission count:" + input.getPermissions().size());
		
		Iterator<Permission> itr = input.getPermissions().iterator();
		while(itr.hasNext()){
			Permission permission = itr.next();
			if(permission.getUsers().size() < Parameters.minUsersForRole.getValue()){
				// odebereme to oprávnění
				itr.remove();
				removedPermissions++;
			}
		}
		
		LOGGER.info("Removed permissions:" + removedPermissions);
		
		
		return removedPermissions;
		
	}
	
	/**
	 * Vytvoření sequnce, které mohou být kandidáty na role
	 * @param input
	 * @return 
	 */
	public static List<Sequence> createSequences(Source input){
		
		List<Sequence> resultSequencesList = new ArrayList<Sequence>();
		
		Iterator<Permission> permIterator = input.getPermissions().iterator();
		while(permIterator.hasNext()){
			
			Permission firstPerm = permIterator.next();	
			
			// sequence
			Sequence sequence = new Sequence();
			// první prvek
			sequence.addPermission(firstPerm);
			
			/**
			 * Pro všechny uživatele, kteří vlastní toto oprávnění
			 */
			for(User user : firstPerm.getUsers()){
				/**
				 * Pro všechny oprávnění, které má tento uživatel
				 */
				for(Permission secondPerm :  user.getPermissions()){
					sequence.addPermission(secondPerm);
					
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
			if(sequence.isValid()){
				// zkontrolujeme velikost
				if(sequence.size() >= Parameters.minUsersForRole.getValue()){
					if(!resultSequencesList.contains(sequence))
						resultSequencesList.add(sequence);
				}
			}
			
		}
		return resultSequencesList;
		
	}
	
}
