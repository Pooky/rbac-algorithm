package edu.jcu.rbac;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

	/**
	 * 
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		
		Logger logger  = Logger.getLogger(Main.class.getName());

		
		Source source = new Source();

		Controller controller = new Controller();
		
		controller.initUserData(source.getDataSet1());
		controller.prepareData();
		controller.extractRolesFromUser();
		controller.solveRolesParents();
		
		
		System.out.println("done");
		

		
	}

}
