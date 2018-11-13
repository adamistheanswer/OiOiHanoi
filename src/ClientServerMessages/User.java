package ClientServerMessages;

import java.io.Serializable;
import java.sql.Date;

/**
 * @author lxf736
 * @version 2018-03-04
 */

public class User implements Serializable {

    private int id;
    private String email;
    private String username;
    private Date dateCreated;
    private int avatar;
    private User user = this;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public int getAvatar() {
        return avatar;
    }

    public User (int id, String email, String username, Date dateCreated, int avatar, String status) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.dateCreated = dateCreated;
        this.avatar = avatar;
        this.status = status;
    }

    public int getID() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return username;
    }

    public User getUser(){
        return user;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!User.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final User other = (User) obj;
        if ((this.username == null) ? (other.username != null) : !this.username.equals(other.username)) {
            return false;
        }
        return true;
    }

}
