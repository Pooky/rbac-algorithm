package edu.jcu.rbac;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.StopWatch;

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
			if(permission.getUsers().size() < Utils.getConfig().getMinUsersForRole()){
				
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
	public List<List<Permission>> joinPermissionLists(){
		
		List<List<Permission>> resultUniqueLists = new ArrayList<List<Permission>>();
		
		Iterator<User> itr = source.getUsers().iterator();
		while(itr.hasNext()){
			
			User user = itr.next();
			if(user.getPermissions().size() >= Utils.getConfig().getMinRoleSize()){
			
				if(!resultUniqueLists.contains(user.getPermissions())){
					resultUniqueLists.add(user.getPermissions());
				}
				
			}else{
				cleaner.addRestPermissions(user.getPermissions(), user);
			}
		}
		
		return resultUniqueLists;
		
	}
	
	/**
	 * Vytvoření sequnce, které mohou být kandidáty na role
	 * @param input
	 * @return 
	 * @return 
	 */
	
	public List<Sequence> createSequences(List<List<Permission>> lists){
		
		final StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		
		LOGGER.info("Starting sequences2 : " + stopwatch);
		
		List<Sequence> resultSequencesList = new ArrayList<Sequence>();
		int i = 1;
		for(List<Permission> list : lists){
			 
			Iterator<Permission> itr = list.iterator();
			LOGGER.info("Processing list: " + i + "/" + lists.size() + "(" + list.size() + ") :" + stopwatch); i++;
			while(itr.hasNext()){
				
				Sequence seq = new Sequence(); 
				Permission startingPermission = itr.next();
				seq.addPermission(startingPermission);
				
				for(Permission perm : list){
					
					if(perm != startingPermission);
					seq.addPermission(perm);
					if(!seq.isValid()){
						seq.removePermission(perm);
					}
					
				}
				
				// zkontrolujeme velikost
				if(seq.size() >= Utils.getConfig().getMinRoleSize() && seq.size() < 10){
					if(!resultSequencesList.contains(seq))
						resultSequencesList.add(seq);
				}	
			}			
		}
		
		LOGGER.info("Generating sequences finished: " + stopwatch);
		
		return resultSequencesList;
		
	}
	
}
