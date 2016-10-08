package com.embarcadero.interview.handson;

import java.util.Date;

/**
 * User Model Object to load from CSV.
 */
public class ImpUser implements User {

    public ImpUser(String firstName, 
            String lastName, 
            String loginName, 
            String password, 
            Date date, 
            boolean enabled) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.loginName = loginName;
        this.password = password;
        this.date = date;
        this.enabled = enabled;
    }

    private String firstName;
    private String lastName;
    private String loginName;
    private String password;// TODO byte[] 
    private Date date;
    private boolean enabled;

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getLoginName() {
        return loginName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @param loginName the loginName to set
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
