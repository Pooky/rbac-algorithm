package edu.jcu.rbac.common;

public enum Parameters {
	minRoleSize(3), maxRoleSize(8), optimalRoleSize(4), minUsersForRole(2);
	//minRoleSize(5), maxRoleSize(15), optimalRoleSize(8), minUsersForRole(4);
	//minRoleSize(3), maxRoleSize(8), optimalRoleSize(3), minUsersForRole(3);

	private final int value;
    private Parameters(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
