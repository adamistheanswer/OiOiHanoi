package ClientServerMessages;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author lxf736
 * @version 2018-03-04
 */

public class Conversation implements Serializable {

    private int id;
    private ArrayList<User> participants;
    private ArrayList<UserMessage> convoUserMessages = new ArrayList<UserMessage>();
    private boolean isGroup;


    public Conversation (int id, ArrayList<User> participants, ArrayList<UserMessage> convoUserMessages) {
        this.id = id;
        this.participants = participants;
        this.convoUserMessages = convoUserMessages;
        this.isGroup = isGroup;
        if (this.convoUserMessages == null) {
            this.convoUserMessages = new ArrayList<UserMessage>();
        }
    }

    public ArrayList<User> getParticipants() {
        return this.participants;
    }

    public ArrayList<UserMessage> getConvoUserMessages() {
        return convoUserMessages;
    }

    public int getId() {
        return this.id;
    }

    public void addMessage(UserMessage message) {
        this.convoUserMessages.add(message);
    }

}
