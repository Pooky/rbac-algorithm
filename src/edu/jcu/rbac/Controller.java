package edu.jcu.rbac;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class Controller {

	private static final Logger LOGGER = Logger.getLogger( Controller.class.getName() );
	
	public static final int minRoleSize = 3; // minimální velikost
	public static final int optimalRoleSize = 4; // optimální velikost
	
	private List<User> users; 
	private Set<Role> uniqueRoles; // role po extrakci
	private List<RestPermission> restPermissions;
	
	private List<Role> uniqueSortedRoles; // unikátní setříděné role po extrakci
	private List<Role> cleanedRoles; // hotové očištěné role
	
	/**
	 * Constructor pro inicializaci controlleru
	 */
	public Controller(){
			
		this.users = new ArrayList<User>();
		this.uniqueRoles = new HashSet<Role>();
		this.restPermissions = new ArrayList<RestPermission>();
		this.cleanedRoles = new ArrayList<Role>();
		
	}


	/**
	 * Extract all roles from set
	 * 
	 * První krok algoritmu
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void extractAllRoles() {
		
		SubsetExample subsetHelper = new SubsetExample();
		
		for(User user : users){
			
			//System.out.println("Current user: " + user.getName());
			// vyber všechna oprávnění větší než k
			if(user.getPermissions().size() < minRoleSize) {
				addRestPermissions(user.getPermissions(), user);
				continue;
			}
			// zpracovani opravneni
			subsetHelper.setInput(user.getPermissions());
			
			for(int roleMaxSize = minRoleSize; roleMaxSize <= user.getPermissions().size(); roleMaxSize++){
				
				// tvoříme skupiny pro konkrétní i
				//System.out.println("Groups of size " + roleMaxSize);
				subsetHelper.setK(roleMaxSize);
				subsetHelper.processSubsets();
				
				// vypsání rolí
				for(List<Permission> z : subsetHelper.getSubsets()){
					
					// nová role
					Role role = new Role();
					role.setPermissions( new HashSet<Permission>(z));
					role.addUser(user);
					
					// pokusíme se přidat novou roli
					if(!uniqueRoles.add(role)){
						// pokud už taková role existuje, přidáme uživatele i k ní
						for(Role myRole : uniqueRoles){
							if(myRole.equals(role)){
								myRole.users.add(user);
							}
						}
					}
				}
			}		
		}
		// setřídíme role
		uniqueSortedRoles = sortRoles(uniqueRoles);
		printRoles(uniqueSortedRoles);
		printRestPermissions();
		
	}

	/**
	 * Tato funkce iteruje přes list rolí, které jsme extrahovali
	 * následně každou roli porovná a vyřeší hiearchické členění parent -> child
	 * v dalším kroku jsou tyto role očištěny a uloženy do pole
	 * CleanedRoles, kde jsou také setříděny
	 * 
	 */
	public void solveRolesParents(){
		
		Iterator<Role> firstIterator, secondIterator;
		Role currentRole, roleToTest = null;

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
		     					addRestPermission(rest, roleToTest.getUsers());
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
		cleanRestPermissions(); // očištění
		
		printRoles(cleanedRoles);
		printRestPermissions();
	}
	
	private void cleanRestPermissions(){
	
		for(Role role : cleanedRoles){
			cleanRestPermission(role.getPermissions(), role.getUsers());
		}
		
	}
	
	private void cleanRestPermission(Set<Permission> permissions, List<User> users) {
		
		Iterator<RestPermission> itr = restPermissions.iterator();
		while(itr.hasNext()){
			RestPermission rest = itr.next();
			for(Permission perm : permissions){
				if(rest.getPermission().equals(perm)){
					rest.removeAllUsers(users);
				}
			}
			// nemáme už žádné uživatele, odebereme oprávnění
			if(rest.getUsers().size() == 0)
				itr.remove();
		}
		
	}


	private void printRestPermissions() {
		
		// print rest permissions
		System.out.println("REST Permissions");
		for(RestPermission restPerm : restPermissions){
			System.out.println(restPerm.toString());
		}
		
		
	}


	/**
	 * Vyřešení hiearchické závislosti rolí
	 * @deprecated
	 * @param currentRole
	 * @param roleToTest
	 * @return 
	 */
	private boolean solveRoleHiearchy(Role currentRole, Role roleToTest) {
		
		boolean wasListChanged = false;
		
		// aktuální role je rodič
		if(currentRole.isParent(roleToTest)){
			if(canWeSplitRoles(currentRole, roleToTest) == false){
				wasListChanged = false;
			}else{
				splitRoles(currentRole, roleToTest);
				wasListChanged = true;
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
					addRestPermission(rest, roleToTest.getUsers());
				}
				
				
			}else{
				splitRoles(roleToTest, currentRole);
				wasListChanged = true;
			}
		}
		
		return wasListChanged;
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
		if((roleToBeSplited.getPermissions().size() - roleToKeep.getPermissions().size()) < minRoleSize){
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


	/**
	 * Přidání oprávnění do zbytkových a přiřazení jich k uživateli
	 * @param permissions
	 * @param user
	 */
	private void addRestPermissions(List<Permission> permissions, User user) {
		for(Permission permission : permissions){
			addRestPermission(permission, user);
		}
	}
	
	private void addRestPermission(Permission permission, User user){
		
		RestPermission restPermission = new RestPermission(permission, user);
		
		if(restPermissions.contains(restPermission)){
			restPermissions.get(restPermissions.indexOf(restPermission)).addUser(user);
		}else{
			restPermissions.add(restPermission);
		}
		
	}
	private void addRestPermissionsFromRole(Role role) {
		
		for(Permission permission : role.getPermissions()){
			addRestPermission(permission, role.getUsers());
		}
		
	}
	/**
	 * Přidáme všechny uživatele k jednomu oprávnění
	 * @param permission
	 * @param users
	 */
	private void addRestPermission(Permission permission, List<User> users) {
			
		for(User user: users){
			addRestPermission(permission, user);
		}
		
	}

	private List<Role> sortRoles(Set<Role> roles) {
		return sortRoles(new ArrayList<Role>(uniqueRoles));
	}
	/**
	 * Setřídění rolí podle počtu uživatelů v roli
	 * @return 
	 */
	protected List<Role> sortRoles(List<Role> rolesToSort){
		
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
		    		
		    		int diffA = Math.abs(roleA.getPermissionsCount() - optimalRoleSize);
		    		int diffB = Math.abs(roleB.getPermissionsCount() - optimalRoleSize);
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
	/**
	 * Naplnění dat a vytvoření uživatelů
	 */
	protected void initUserData(){
		
		Permission outlook = new Permission("Outlook");
		Permission shareDrive = new Permission("Sharedrive");
		Permission codeAcademy = new Permission("Code Academy");
		Permission svn = new Permission("SVN Access");
		Permission hrTools = new Permission("HR Tools");
		Permission csharp = new Permission("C# Tools");
		Permission msAccess = new Permission("MS Accesss");
		Permission eLearning = new Permission("E-learning");
		Permission oracleBiSuite = new Permission("Oracle BI Suite");
		Permission eclipse = new Permission("Eclipse");
		Permission hrPortal = new Permission("HR Portal");
		Permission basicCourses = new Permission("Basic courses");
		
		
		Role basicRole = new Role();
		basicRole.addPermission(outlook);
		basicRole.addPermission(shareDrive);
		basicRole.addPermission(eLearning);
		basicRole.addPermission(basicCourses);

		Role HRRole = new Role();
		HRRole.addPermission(hrTools);
		HRRole.addPermission(msAccess);
		HRRole.addPermission(hrPortal);
		
		Role BiAnalysis = new Role();
		BiAnalysis.addPermission(svn);
		BiAnalysis.addPermission(msAccess);
		BiAnalysis.addPermission(oracleBiSuite);
		
		Role CSharpDeveloper = new Role();
		CSharpDeveloper.addPermission(codeAcademy);
		CSharpDeveloper.addPermission(svn);
		CSharpDeveloper.addPermission(csharp);
		
		Role JavaDeveloper = new Role();
		JavaDeveloper.addPermission(codeAcademy);
		JavaDeveloper.addPermission(svn);
		JavaDeveloper.addPermission(eclipse);
		
		// Vytvoření uživatelů
		users.add(new User("HR 1").addRole(basicRole).addRole(HRRole));
		users.add(new User("HR 2").addRole(basicRole).addRole(HRRole));
		users.add(new User("BI 1").addRole(basicRole).addRole(BiAnalysis));
		users.add(new User("C# developer 1").addRole(basicRole).addRole(CSharpDeveloper));
		users.add(new User("Java Developer 1").addRole(basicRole).addRole(JavaDeveloper));
		users.add(new User("Java Developer 2").addRole(basicRole).addRole(JavaDeveloper));
		users.add(new User("HR 3").addRole(basicRole).addRole(HRRole));
		users.add(new User("HR 4").addRole(basicRole).addRole(HRRole));
		users.add(new User("C# developer 2").addRole(basicRole).addRole(CSharpDeveloper));
		users.add(new User("C# developer 3").addRole(basicRole).addRole(CSharpDeveloper));
		users.add(new User("BI 2").addRole(basicRole).addRole(BiAnalysis));
		users.add(new User("Wrong user").addPermission(outlook).addPermission(eLearning));

	}	
	
	
}
