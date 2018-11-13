package ClientServerMessages;

import java.io.Serializable;

/**
 * @author lxf736
 * @version 2018-03-01
 */

public class StartConversationMessage extends Message implements Serializable {

    private User sender;
    private User recipient;
    private Conversation conversation;
    private boolean newMessage;

    public StartConversationMessage(User sender, User recipient, boolean newMessage) {
        super("New Conversation");
        this.sender = sender;
        this.recipient = recipient;
        this.newMessage = newMessage;
    }

    public User getSender() {
        return sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setResponse(String responseMessage, boolean responseSuccess, Conversation conversation) {
        super.setResponse(responseMessage, responseSuccess);
        this.conversation = conversation;
    }
}
