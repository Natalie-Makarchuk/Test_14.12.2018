package com.teaminternational.coe.entities.users;

import com.teaminternational.coe.enums.Users;

/**
 * Class for user entity.
 * Created for using in other classes of framework. Contain username and password fields and methods for storing and getting those fields.
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 */
public class User {

    /**
     * Field for User name
     */
    private String username;
    /**
     * Field for User password
     */
    private String password;

    /**
     * Assign field user password getting it from provided Users object
     *
     * @param user Users object
     */
    public User(Users user) {
        this.password = user.getPassword();
    }

    /**
     * Assign user name and password fields
     *
     * @param username User name
     * @param password User password
     */
    protected User(String username,
                   String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Get user name field
     *
     * @return user name
     */
    public String getEmail() {
        return username;
    }

    /**
     * Get user password field
     *
     * @return user password
     */
    public String getPassword() {
        return password;
    }


}
