package edu.jcu.rbac.common;

import java.io.File;
import java.io.IOException;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

public class Config {

	private int min_role_size;
	private int max_role_size;
	private int optimal_role_size;
	private int min_users_for_role;
	
	private String dataSetPath;
	private String outputFolder;
	
	public Config(String filePath){
		
		String section = "config";
		
		Wini ini = null;
		// rozparsování configu a načtení hodnot
		try {
			 ini = new Wini(new File(filePath));
		} catch (InvalidFileFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.setMinRoleSize(ini.get(section, "min_role_size", int.class));
		this.setMaxRoleSize(ini.get(section, "max_role_size", int.class));
		this.setOptimalRoleSize(ini.get(section, "optimal_role_size", int.class));
		this.setMinUsersForRole(ini.get(section, "min_users_for_role", int.class));
		
		this.setDataSetPath(ini.get(section, "dataset_file", String.class));	
		this.setOutputFolder(ini.get(section, "output_folder", String.class));		
		
	}
	
	
	public int getMinRoleSize() {
		return min_role_size;
	}
	public void setMinRoleSize(int min_role_size) {
		this.min_role_size = min_role_size;
	}
	public int getOptimalRoleSize() {
		return optimal_role_size;
	}
	public void setOptimalRoleSize(int optimal_role_size) {
		this.optimal_role_size = optimal_role_size;
	}
	public int getMaxRoleSize() {
		return max_role_size;
	}
	public void setMaxRoleSize(int max_role_size) {
		this.max_role_size = max_role_size;
	}
	public int getMinUsersForRole() {
		return min_users_for_role;
	}
	public void setMinUsersForRole(int min_users_for_role) {
		this.min_users_for_role = min_users_for_role;
	}


	public String getDataSetPath() {
		return dataSetPath;
	}


	public void setDataSetPath(String dataSetPath) {
		this.dataSetPath = dataSetPath;
	}


	public String getOutputFolder() {
		return outputFolder;
	}


	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}
	
	
	
}
