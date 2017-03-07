package edu.jcu.rbac;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.StopWatch;

import edu.jcu.rbac.common.Parameters;
import edu.jcu.rbac.common.Utils;
import edu.jcu.rbac.elements.Permission;
import edu.jcu.rbac.elements.RestPermission;
import edu.jcu.rbac.elements.Sequence;
import edu.jcu.rbac.elements.User;

public class RulesManager {
	
	private static final Logger LOGGER = Logger.getLogger(RulesManager.class.getName() );
	
	private Cleaner cleaner;
	private Source source;
	
	public RulesManager(Cleaner cleaner, Source source) {
	
		this.cleaner = cleaner;
		this.source = source;
		
	}

	/**
	 * Odebrání oprávnění, které nemohou tvořit role
	 * @return
	 */
	public void removePermissionWhichCantMakeRole(){
			
		int removedPermissions = 0;
		
		LOGGER.info("Permission count:" + source.getPermissions().size());
		
		Iterator<Permission> itr = source.getPermissions().iterator();
		while(itr.hasNext()){
			Permission permission = itr.next();
			if(permission.getUsers().size() < Parameters.minUsersForRole.getValue()){
				
				cleaner.addRestPermission(permission, permission.getUsers());
				// odebereme to oprávnění
				itr.remove();
				removedPermissions++;
			}
		}
		
		//LOGGER.info("Removed permissions:" + removedPermissions);
		LOGGER.info("Removed permissions: " + Utils.toString(cleaner.getRestPermissions()));
		
	}
	
	/**
	 * Vytvoření seqvenkcí, z kterých budou následně extrahovány kandidátní role
	 * @return 
	 */
	public List<Sequence> createSequences(){
		
		List<Sequence> resultSequencesList = new ArrayList<Sequence>();
		
		Iterator<User> itr = source.getUsers().iterator();
		while(itr.hasNext()){
			
			User user = itr.next();
			Sequence sequence = new Sequence(user.getPermissions());
			
			if(sequence.size() >= Parameters.minRoleSize.getValue()){
			
				if(!resultSequencesList.contains(sequence)){
					resultSequencesList.add(sequence);	
				}
				
			}else{
				cleaner.addRestPermissions(user.getPermissions(), user);
			}
		}
		
		return resultSequencesList;
		
	}
	
	/**
	 * Vytvoření sequnce, které mohou být kandidáty na role
	 * @param input
	 * @return 
	 * @return 
	 */
	
	public List<Sequence> createSequences2(){
		
		final StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		
		LOGGER.info("Starting sequences2 : " + stopwatch);
		
		List<Sequence> resultSequencesList = new ArrayList<Sequence>();
		List<Permission> testedPermissions = new ArrayList<Permission>();
		
		Iterator<Permission> permIterator = source.getPermissions().iterator();
		while(permIterator.hasNext()){
			
			Permission firstPerm = permIterator.next();	
			for(User user : firstPerm.getUsers()){
				
				LOGGER.info("Extracting sequence " + user.getIdentifier() + " perm: " + firstPerm.getIdentifier()); 
				LOGGER.info("Stopwatch: " + stopwatch);
				
				// sequence
				Sequence sequence = new Sequence();
				// první prvek
				sequence.addPermission(firstPerm);

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
