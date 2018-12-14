package com.teaminternational.coe.entities.users;


import com.teaminternational.coe.enums.Users;
import com.teaminternational.coe.utils.CommonHelper;
import org.testng.Reporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Class for collection of users.
 * Managing two lists of users for running tests. One for available for tests users and second list for already used users in tests.
 * Can pick users fot tests from available list by name or by object of User class and release users from list of currently used users back to available users list.
 * Initial set of users for available users list taken from enum Users
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 * @see Users
 */
public class UsersPool {
    /**
     * Static variable for singleton instance of UsersPool class
     */
    private static UsersPool instance;
    /**
     * List for available users
     */
    private List<User> availableUsersPool;
    /**
     * List for currently used users
     */
    private List<User> currentlyUsingUsersPool;

    /**
     * constructor calls init method
     */
    private UsersPool() {
        init();
    }

    /**
     * Create a new UsersPool instance if it's not created before and store in private static variable
     *
     * @return created instance of UsersPool
     */
    public static UsersPool getInstance() {
        if (instance == null) {
            instance = new UsersPool();
        }
        return instance;
    }

    /**
     * Initiate both lists of users: available and currently used
     */
    private void init() {
        this.availableUsersPool = Collections.synchronizedList(new ArrayList<>());
        this.currentlyUsingUsersPool = Collections.synchronizedList(new ArrayList<>());
        //Fill available users
        Arrays.stream(Users.values())
                .forEach(i -> availableUsersPool.add(new User(i)));

    }

    /**
     * Get user by user name. Take it from available list and put to currently used.
     * Write report log about it.
     * If user not found in available write log message about it and tries to get it from currently used.
     *
     * @param userName  User name
     * @return found user or null
     */
    public synchronized User getUser(String userName) {
        User outputUser;
        List<User> output = availableUsersPool.stream().filter(i -> i.getEmail().contains(userName)).collect(Collectors.toList());
        if (output.size() == 0) {
            Reporter.log("No available users in pool", 1, true);
            outputUser = currentlyUsingUsersPool.stream().filter(i -> i.getEmail().contains(userName)).findFirst().orElse(null);
        } else {
            Reporter.log("Getting user from pool", 1, true);
            outputUser = output.get(CommonHelper.getRandomIntBetween(0, output.size()));
        }
        Reporter.log("Using " + outputUser.getEmail() + " For test", 1, true);
        availableUsersPool.remove(outputUser);
        currentlyUsingUsersPool.add(outputUser);
        return outputUser;
    }


    /**
     * Get user by user object. Take it from available list and put to currently used.
     * Write report log about it.
     * If user not found in available write log message about it.
     *
     * @param user  User class
     *
     * @return found user or null
     */
    public synchronized User getUser(User user) {
        User output = availableUsersPool.stream().filter(i -> i.equals(user)).findFirst().orElse(user);
        Reporter.log("Using " + output.getEmail() + " For test", 1, true);
        try {
            availableUsersPool.remove(output);
        } catch (Exception e) {
            Reporter.log("User " + user.getEmail() + " is already in use", 1, true);
        }
        currentlyUsingUsersPool.add(output);
        return output;
    }

    /**
     * Remove user from currently used list and put to available list.
     * Write report log about it.
     * If user is null, write report log about it.
     *
     * @param user  User class
     */
    public synchronized void releaseUser(User user) {
        if (user != null) {
            Reporter.log("Releasing " + user.getEmail() + " from pool", 1, true);
            currentlyUsingUsersPool.remove(user);
            availableUsersPool.add(user);
        } else {
            Reporter.log("User was not found", 1, true);
        }
    }
}
