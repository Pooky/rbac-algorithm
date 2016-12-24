package edu.jcu.rbac.model;

/**
 * Konkrétní abstrakce oprávnění
 * @author martin.klima
 *
 */
public class Permission {
	
	private String name;
	private Long id;

	public Permission(String name, Long id){
		this.setName(name);
		this.setId(id);
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
