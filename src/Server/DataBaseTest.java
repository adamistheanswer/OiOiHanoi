package Server;

import ClientServerMessages.*;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

public class DataBaseTest {

    public static void main (String [] args) {

        Connection connection = null;
        try {
            connection = Database.connectToDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int id = Database.retrieveIDEmail(connection, "flee17@hotmail.co.uk");
        System.out.println(id);

        //get sending User from DB, get receiving User from DB,
//        User sendingUser = Database.retrieveUser(connection, 1);
//        System.out.println("user " + sendingUser.getUsername() + " retrieved from database");
//        User receivingUser = Database.retrieveUser(connection, 3);
//        System.out.println("recipient " + receivingUser.getUsername() + " retrieved from database");
//
//        Database.retrieveMessages(connection, 103);

        //System.out.println(Database.conversationPresent(connection, sendingUser, receivingUser));


//        //Start convo between 2 users
//        StartConversationMessage startConversationMessage = new StartConversationMessage(sendingUser, receivingUser);
//       // Database.startConversation(connection, startConversationMessage.getSender(), startConversationMessage.getRecipient());
//
//        //Retrieve convo from DB
//        Conversation conversation = Database.retrieveConversation(connection, startConversationMessage.getSender(),
//                startConversationMessage.getRecipient());
//        System.out.println(conversation.getParticipants().get(0).toString());
//
//        //getconversation ID from convo started above and send a message
//        MessagingMessage messagingMessage = new MessagingMessage(sendingUser, "first message in conversation",  receivingUser, conversation.getId() );
//        Database.newMessage(connection, messagingMessage.getSender(), messagingMessage.getRecipient(), messagingMessage.getConversationID(), messagingMessage.getMessageText());
        try {
            connection.close();
            System.out.println("DB connection closed");
        } catch (SQLException e) {
            System.out.println("DB connection NOT closed");
        }

    }
}
