package ClientServerMessages;

/**
 * @author lxf736
 * @version 2018-03-18
 */

public class ChangePasswordMessage extends Message {

    private String userEmail;
    private String code;
    private String newPassword;

    public ChangePasswordMessage(String userEmail, String code, String newPassword) {
        super("reset-password");
        this.userEmail = userEmail;
        this.code = code;
        this.newPassword = newPassword;
    }

    public String getRequestingUser() {
        return userEmail;
    }

    public String getCode() {
        return code;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
