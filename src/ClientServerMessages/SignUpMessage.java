package ClientServerMessages;

import java.io.Serializable;

/**
 * @author lxf736
 * @version 2018-03-01
 */

public class SignUpMessage extends Message implements Serializable {

    private String email;
    private String username;
    private String password;
    private int avatar;

    public SignUpMessage(String email, String username, String password, int avatar) {
        super("sign-up");
        this.email = email;
        this.username = username;
        this.password = password;
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getAvatar() {
        return avatar;
    }

}
