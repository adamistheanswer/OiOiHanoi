package Server;

import ClientServerMessages.User;

import java.util.ArrayList;

/**
 * @author lxf736
 * @version 2018-03-04
 */

public class UserDetails {

    private String displayName;
    private String firstName;
    private String lastName;
    private String bioDesc;
    private ArrayList<User> favoriteContacts;


    // avatar

    public UserDetails(String displayName, String firstName, String lastName, String bioDesc) {
        this.displayName = displayName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bioDesc = bioDesc;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getBioDesc() {
        return bioDesc;
    }

    public void setBioDesc(String bioDesc) {
        this.bioDesc = bioDesc;
    }
}
