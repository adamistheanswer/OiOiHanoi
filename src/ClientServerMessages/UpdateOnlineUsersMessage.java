package ClientServerMessages;

import java.io.Serializable;

/**
 * @author lxf736
 * @version 2018-03-01
 */

public class UpdateOnlineUsersMessage extends Message implements Serializable {

    private User user;
    private boolean login;

    /**
     *
     * @param user
     * @param login - true if this is a login message, false if logoff
     */
    public UpdateOnlineUsersMessage(User user, Boolean login) {
        super("sign-in");
        this.user = user;
        this.login = login;
    }

    public User getUser() {
        return this.user;
    }

    /**
     * @return true if login, false if logoff
     */
    public boolean getLogin() {
        return login;
    }

}
