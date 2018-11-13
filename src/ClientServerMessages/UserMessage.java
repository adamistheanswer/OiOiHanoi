package ClientServerMessages;

import ClientServerMessages.Conversation;
import ClientServerMessages.User;
import Server.UserGroup;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lxf736
 * @version 2018-03-04
 */

public class UserMessage implements Serializable {

    private User sendingUser;
    private int conversationID;
    private String content;
    private java.sql.Timestamp sendTime;
    private String stringTime;

    public User getSendingUser() {
        return sendingUser;
    }

    public String getContent() {
        return content;
    }

    public String getStringTime() {
        return this.stringTime;
    }


    public java.sql.Timestamp getSendTime() {
        return sendTime;
    }


    public UserMessage(User sendingUser, java.sql.Timestamp time, int conversationID, String content) {
        this.sendingUser = sendingUser;
        this.sendTime = time;
        SimpleDateFormat sdfTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        this.stringTime = sdfTime.format(this.sendTime);
        this.conversationID = conversationID;
        this.content = content;
    }


}
