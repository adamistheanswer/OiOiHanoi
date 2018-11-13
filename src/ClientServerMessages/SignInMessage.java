package ClientServerMessages;


import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * @author lxf736
 * @version 2018-03-01
 */

public class SignInMessage extends Message implements Serializable {

    private String username;
    private String password;
    private User user;
    private ArrayList<User> onlineUsers;

    public SignInMessage(String username, String password) {
        super("sign-in");
        this.username = username;
        this.password = password;
    }

    public String getEmail() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public User getUser() {
        return this.user;
    }

    public ArrayList<User> getOnlineUsers() {
        return onlineUsers;
    }

    public void setResponse(String responseMessage, boolean responseSuccess, User user, ArrayList<User> onlineUsers) {
        super.setResponse(responseMessage, responseSuccess);
        this.user = user;
        this.onlineUsers = onlineUsers;
    }

}
