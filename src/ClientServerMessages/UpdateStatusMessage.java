package ClientServerMessages;

public class UpdateStatusMessage extends Message {

    private String status;
    private User user;

    public UpdateStatusMessage(String status, User user) {
        super("Update Status");
        this.status = status;
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

}
