package ClientServerMessages;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author lxf736
 * @version 2018-03-15
 */

public class MessagingMessage extends Message implements Serializable {

    private User sender;
    private String messageText;
    private User recipient;
    private int conversationID;
    private UserMessage userMessage;
    private Conversation conversation;

    public MessagingMessage(User sender, String messageText, User recipient, int conversationID) {
        super("Messaging-UserMessage");
        this.sender = sender;
        this.messageText = messageText;
        this.recipient = recipient;
        this.conversationID = conversationID;
    }

    public User getSender() {
        return sender;
    }

    public String getMessageText() {
        return messageText;
    }

    public User getRecipient() {
        return recipient;
    }

    public int getConversationID() {
        return conversationID;
    }

    public UserMessage getUserMessage() {
        return userMessage;
    }

    public void setResponse (String responseMessage, boolean responseSuccess, Conversation conversation) {
        super.setResponse(responseMessage, responseSuccess);
        this.conversation = conversation;
    }

    public Conversation getConversation() {
        return conversation;
    }
}