package edu.jcu.rbac;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import edu.jcu.rbac.combinations.SubsetExample;
import edu.jcu.rbac.common.Utils;
import edu.jcu.rbac.elements.Permission;
import edu.jcu.rbac.elements.RestPermission;
import edu.jcu.rbac.elements.Role;
import edu.jcu.rbac.elements.User;

public class RoleSolver {

	private static final Logger LOGGER = Logger.getLogger( RoleSolver.class.getName() );

	private List<Role> candidateRoles; // role po extrakci
	private List<RestPermission> restPermissions;
	
	private List<Role> uniqueSortedRoles; // unikátní setříděné role po extrakci
	private List<Role> cleanedRoles; // hotové očištěné role

	/**
	 * Constructor pro inicializaci controlleru
	 */
	public RoleSolver(List<Role> candidateRoles) {
		
		this.candidateRoles = candidateRoles;
		restPermissions = new ArrayList<RestPermission>();
		uniqueSortedRoles = new ArrayList<Role>();
		cleanedRoles = new ArrayList<Role>();

		LOGGER.info("Starting controller");
	}
	/**
	 * Tato funkce iteruje přes list rolí, které jsme extrahovali
	 * následně každou roli porovná a vyřeší hiearchické členění parent -> child
	 * v dalším kroku jsou tyto role očištěny a uloženy do pole
	 * CleanedRoles, kde jsou také setříděny
	 * @return 
	 * 
	 */
	public List<Role> solveRolesParents(){
		
		LOGGER.info("Solving role parents and cleaning");
		
		Iterator<Role> firstIterator, secondIterator;
		Role currentRole, roleToTest = null;
		Cleaner cleaner = new Cleaner(this.restPermissions);
		
		uniqueSortedRoles = sortRoles(candidateRoles);

		Boolean allClean = false;
		// dokud není čisto
		while(!allClean) {
			
			 allClean = true;
			 // projdeme všechny role a první vezmeme
			 firstIterator = uniqueSortedRoles.iterator();
			 while(firstIterator.hasNext()){
				 
		         currentRole = firstIterator.next();
		         
		         // existuje role, která má podobné elementy?
		         secondIterator = uniqueSortedRoles.iterator();
		         while(secondIterator.hasNext()){
		        	 
		        	 roleToTest = secondIterator.next();
		        	 // stejné role nebudeme řešit
		        	 if(roleToTest == currentRole){
		        		 if(secondIterator.hasNext()){
		        			 roleToTest = secondIterator.next();
		        		 }else{
		        			 // už není co procházet
		        			 break;
		        		 }
		        		 
		        	 }
		        	 
		     		// aktuální role je rodič
		     		if(currentRole.isParent(roleToTest)){
		     			if(canWeSplitRoles(currentRole, roleToTest) == false){
		     				// nothing to do
		     			}else{
		     				splitRoles(currentRole, roleToTest);
		     				allClean = false;
		     				break;
		     			}
		     		}
		     		
		     		// aktuální role má nadřazenou roli podsebou
		     		if(roleToTest.isParent(currentRole)){
		     			if(canWeSplitRoles(roleToTest, currentRole) == false){
		     				// nelze role rozdělit, proto zachováme podřazenou roli a rozdělíme nadřazenou
		     				// všem uživatelům přidáme roli podřazenou a zbytek oprávnění vložíme do zbytkových
		     				
		     				List<Permission> permissionsToSave = new ArrayList<Permission>(roleToTest.getPermissions());
		     				permissionsToSave.removeAll(currentRole.getPermissions());
		     				
		     				// přiřadíme podřazené roli uživatele nadřazené role
		     				currentRole.addUsers(roleToTest.getUsers());
		     				
		     				// provedeme vymazání role a uložení oprávnění do zbytkových
		     				for(Permission rest : permissionsToSave){
		     					cleaner.addRestPermission(rest, roleToTest.getUsers());
		     				}
		     				// odebereme roli
		     				secondIterator.remove();		     				
		     				
		     			}else{
		     				splitRoles(roleToTest, currentRole);
		     			}
		     			allClean = false;
	     				break;
		     		}	
		        	 
		        	 //INFO Zjistili jsme, že testovaná role není ani child a ani rodič,
		        	 // 	nyní můžeme ověřit, jestli má některá oprávnění, z testované role
		        	 //		pokud ano, má taktéž všechny uživatele?
		        	 
		        	 if(roleToTest.containsAnyOfThisPermissions(currentRole.getPermissions())){
		        		 if(roleToTest.containsAllUsers(currentRole.getUsers())){
		        			 LOGGER.info("Removing role: " + roleToTest.toString() + " becouse is duplicit of " + currentRole.toString());
		        			 // odebereme tuto roli, protože je duplicitní
		        			 secondIterator.remove();
		        			 allClean = false;
		        		 }
		        	 }
		        	 
		         }		   
		         // reset listů, protože došlo k přidání či rozdělení role
		         if(!allClean){
			        	 // znovu setřídíme
			        	 sortRoles(uniqueSortedRoles);
			        	 firstIterator = uniqueSortedRoles.iterator();
			        	 break;
		         }else{
		        	 	cleanedRoles.add(currentRole);
		        	 	firstIterator.remove();
		         }
			 }
	      }
		
		//znova setřídíme
		sortRoles(cleanedRoles);
		
		cleaner.cleanRestPermissions(cleanedRoles);
		
		printRoles(cleanedRoles);
		printRestPermissions();
		
		int i = 1;
		// pridani ID
		for(Role role : cleanedRoles){
			role.setId(i);
			i++;
		}
		
		return cleanedRoles;
	}

	private void printRestPermissions() {
		
		// print rest permissions
		System.out.println("REST Permissions");
		for(RestPermission restPerm : restPermissions){
			System.out.println(restPerm.toString());
		}
		
		
	}
	/**
	 * Funkce která ověří, jestli se dvě role dají dělit
	 * Ověřuje velikost oprávnění po extrakci jedné role z druhé musí být větší nebo rovna k
	 * @param roleToKeep
	 * @param roleToBeSplited
	 * @return
	 */
	private boolean canWeSplitRoles(Role roleToBeSplited, Role roleToKeep){
		
		// test jestli můžeme role dělit, k je nejmenší velikost role
		if((roleToBeSplited.getPermissions().size() - roleToKeep.getPermissions().size()) < Utils.getConfig().getMinRoleSize()){
			return false;
		}else{
			return true;
		}
		 
	}
	
	/**
	 * Samotné rozdělení role nadřazené do role podřazené
	 * probíhá pomocí odebrání a přidání oprávnění, role samotné se nemažou
	 * Následně se projde pole a ověří se, že při přidání nových oprávnění, jsou role stále unikátní
	 * a nejsou duplicitní.
	 * @param parentRole
	 * @param subRole
	 */
	private void splitRoles(Role parentRole, Role subRole){

		// odebereme oprávnění child role
		parentRole.getPermissions().removeAll(subRole.getPermissions());
		// přidej podřazené roli uživatele nadřazené role
		subRole.addUsers(parentRole.getUsers());
		
		// projdeme znovu seznam rolí a případně odstraníme diplicity
		Iterator<Role> itr = uniqueSortedRoles.iterator();
		while(itr.hasNext()){
			Role role = itr.next();
			if(role.equals(parentRole) && role != parentRole){
				parentRole.addUsers(role.getUsers());
				itr.remove();
			}
		}
	}
	/**
	 * Vypsání listu rolí
	 * @param roles
	 */
	private void printRoles(List<Role> roles){
	
		System.out.println("Size: " + roles.size());
		for(Role role : roles){
			System.out.println(role.toString());
			System.out.println(role.getUsersAsString());

		}
		
	}
	
	private void printRoles(Set<Role> roles) {
		
		System.out.println("Size: " + roles.size());
		for(Role role : roles){
			System.out.println(role.toString());
		}
	}


	private List<Role> sortRoles(Set<Role> roles) {
		return sortRoles(new ArrayList<Role>(candidateRoles));
	}
	/**
	 * Setřídění rolí podle počtu uživatelů v roli
	 * @return 
	 */
	protected List<Role> sortRoles(List<Role> rolesToSort){
		
		LOGGER.info("Starting sorting of list size: " + rolesToSort.size());
		
		Collections.sort(rolesToSort, new Comparator<Object>() 
		{
		    public int compare(Object o1, Object o2) 
		    {
		    	Role roleA = (Role)o1;
		    	Role roleB = (Role)o2;
		    	// setřídění
		    	if(roleA.getUsers().size() < roleB.getUsers().size()){
		    		return 1;
		    	}else if(roleA.getUsers().size() > roleB.getUsers().size()){
		    		return -1;
		    	}else{
		    		
		    		int diffA = Math.abs(roleA.getPermissionsCount() - Utils.getConfig().getOptimalRoleSize());
		    		int diffB = Math.abs(roleB.getPermissionsCount() - Utils.getConfig().getOptimalRoleSize());
		    		// rozdíl v optimální velikosti
		    		if(diffA < diffB){
		    			return -1;
		    		}else if(diffA > diffB){
		    			return 1;
		    		}else{
		    			return 0;
		    		}
		    		
		    		
		    	}
		    		
		    
		    }
		});
		return rolesToSort;
		
	}
	public List<RestPermission> getRestPermissions() {
		return this.restPermissions;
	}
	public void setRestPermissions(List<RestPermission> restPermissions2) {
		this.restPermissions = restPermissions2;
	}

	
	
}

