package edu.jcu.rbac;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.jcu.rbac.elements.Permission;
import edu.jcu.rbac.elements.Role;
import edu.jcu.rbac.elements.User;

public class Source {
	
	private static final Logger LOGGER = Logger.getLogger( Source.class.getName() );

	private ArrayList<User> users;
	private ArrayList<Permission> permissions; 
	
	public Source(){
		
		LOGGER.setLevel(Level.WARNING);
		users = new ArrayList<User>();
		permissions = new ArrayList<Permission>();
		
	}
	
	
	public List<User> initDataSet1(){
		return this.getDataSet("data/test-data3.csv");
	}
	public List<User> initDataSet2(){
		return this.getDataSet("data/sqlresults/users_permission_from_roles_max_25_permission.csv");
	}
	public List<User> initDataSet3(){
		return this.getDataSet("data/sqlresults/users_permission_from_roles_max_15_permission.csv");
	}
	/**
	 * Vytvoření dat z db export
	 * @return
	 */
	public List<User> getDataSet(String csvFileName){
		
		Map<Integer, Permission> permissions = new HashMap<Integer, Permission>();
		Map<Integer, User> users = new HashMap<Integer, User>();
		
        String csvFile = csvFileName;
        String line = "";
        String cvsSplitBy = ";";
        

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	
        	Integer permId, userId;
            
            Permission perm;
            User user;
            
            int i = 0;
        		
        		while ((line = br.readLine()) != null) {
        			
        			userId = null;
        			permId = null;
        			
                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                if(data.length > 1){
                		LOGGER.log(Level.INFO, "User [id= " + data[0] + " , permission=" + data[1] + "]");
                }
                // přeskočíme první řádek
                	if(data.length > 1 && i != 0){	
                		
					try {  
					 	userId = Integer.parseInt(data[0]);
					 	permId = Integer.parseInt(data[1]);
					} catch (NumberFormatException e) {  
					    LOGGER.log(Level.WARNING, "Řádek č." + i + " se nedá parsovat.", e);
					}
					
					if(userId == null || permId == null){
						continue;
					}               		
                		
                		// check users
                		user = users.get(userId);
                		if(user == null){
                			user = new User("Uživatel " + userId, userId);
                			users.put(userId, user);
                		}
                 		// check permissions
                		perm = permissions.get(permId);
                		if(perm == null){
                			perm = new Permission("Oprávnění " + permId, permId);
                			permissions.put(permId, perm);
                		}else{
                			perm.addUser(user); // uživatel k oprávnění
                		}
                		
                		// vložíme oprávnění k uživateli
                		user.addPermission(perm); 		
                		
                }
                i++;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.users = new ArrayList<User>(users.values());
        this.setPermissions(new ArrayList<Permission>(permissions.values()));
        
		return this.users;
		
	}
	

	/**
	 * Naplnění dat a vytvoření uživatelů
	 * @return 
	 */
	public List<User> getDataMock(){
		
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
		
		List<User> users = new ArrayList<User>(); 
		
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

		
		for(User user : users){
			for(Permission permission : user.getPermissions()){
				permission.addUser(user);
				
				if(!permissions.contains(permission))
					permissions.add(permission);
					
			}
		}
		
		return users;
		
	}
	public ArrayList<Permission> getPermissions() {
		return permissions;
	}
	public void setPermissions(ArrayList<Permission> permissions) {
		this.permissions = permissions;
	}
	public ArrayList<User> getUsers() {
		return users;
	}
	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}	
	
}
