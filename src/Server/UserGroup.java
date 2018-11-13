package Server;

/**
 * @author lxf736
 * @version 2018-03-04
 */

import ClientServerMessages.User;

import java.util.ArrayList;

public class UserGroup {

    private ArrayList<User> participants;
    private String groupName;

    public UserGroup (ArrayList<User> participants, String groupName) {
        this.participants = participants;
    }
}
