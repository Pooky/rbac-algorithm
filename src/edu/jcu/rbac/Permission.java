package edu.jcu.rbac;

/**
 * Konkrétní abstrakce oprávnění
 * @author martin.klima
 *
 */
public class Permission {
	
	private String name;
	private Integer id;

	public Permission(String name, Integer permId){
		this.setName(name);
		this.setId(permId);
	}

	public Permission(String string) {
		this.setName(string);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer permId) {
		this.id = permId;
	}

	public String getIdentifier() {
		return this.name;
	}
	
}
