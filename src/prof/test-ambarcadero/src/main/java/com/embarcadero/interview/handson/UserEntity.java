package com.embarcadero.interview.handson;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Persistent entity for user object
 */
@Entity
@XmlRootElement
public class UserEntity implements User, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="FIRST_NAME")
    private String firstName;
    
    @Column(name="LAST_NAME")
    private String lastName;
    
    @Column(name="LOGIN_NAME")
    private String loginName;
    
    @Column(name="PASSWORD_HASH")
    private String password;// TODO byte[] 
    
    @Column(name="USER_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    
    @Column(name="ENABLED")
    private String enabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserEntity)) {
            return false;
        }
        UserEntity other = (UserEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public UserEntity(String firstName,
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
        this.enabled = Boolean.toString(enabled);
    }

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
        return Boolean.parseBoolean(enabled);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = Boolean.toString(enabled);
    }
}
