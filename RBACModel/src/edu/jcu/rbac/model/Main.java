package edu.jcu.rbac.model;
public class Main {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		Controller controller = new Controller();
		controller.initUserData();
	
		controller.extractAllRoles();
		controller.solveRolesParents();
		
		
		System.out.println("done");
		

		
	}

}
