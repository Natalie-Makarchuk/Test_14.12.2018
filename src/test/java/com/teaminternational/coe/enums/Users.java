package com.teaminternational.coe.enums;

/**
 * Class for defined set of users.
 * This set will be used in the UsersPool class as initial set for available users list
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 * @see com.teaminternational.coe.entities.users.UsersPool
 */
public enum Users {
    /**
     * constant with predefined set of users
     */
    ADMIN("Admin", "Changeit");

    /**
     * user name field
     */
    private String name;
    /**
     * user password field
     */
    private String password;

    /**
     * constructor defines user name and password
     *
     * @param name user name
     * @param password  user password
     */
    Users(String name, String password) {
        this.name = name;
        this.password = password;

    }

    /**
     * Get user name
     * @return user name
     */
    public String getName() {
        return name;
    }

    /**
     * Get user password
     * @return user password
     */
    public String getPassword() {
        return password;
    }

}
