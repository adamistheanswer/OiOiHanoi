package ClientServerMessages;

/**
 * @author lxf736
 * @version 2018-03-18
 */

public class ResetPasswordMessage extends Message {

    private String userEmail;

    public ResetPasswordMessage(String userEmail) {
        super("reset-password");
        this.userEmail = userEmail;
    }

    public String getRequestingUser() {
        return userEmail;
    }
}
