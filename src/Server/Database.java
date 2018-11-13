package Server;

import ClientServerMessages.Conversation;
import ClientServerMessages.User;
import ClientServerMessages.UserMessage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

/**
 * @author Danyal Bajar
 * @version 2018-03-15
 */

public class Database {

    public static Connection connectToDatabase() throws SQLException, IOException {

        Connection connection = null;

        FileInputStream databasePropsInput = new FileInputStream(new File("database.properties"));
        Properties props = new Properties();
        props.load(databasePropsInput);
        databasePropsInput.close();

        String url = props.getProperty("url");
        String driver = props.getProperty("jdbc.drivers");
        String user = props.getProperty("user");
        String password = props.getProperty("password");

        try {
            Class.forName(driver);
            System.out.println("PostgreSQL driver registered.");
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver not found");
        }

        connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(true);
        if (connection != null) {
            System.out.println("Connected to Database");
        }
        return connection;
    }


    public static boolean createUser(Connection con, String username, String password, String emailAddress, int avatar) {
        /* a method to create a new user in database with provided emailAddress & password
         * return true if successful,
         * else false*/

        //gonna have to return current rs of users, so kn ow where to insert new
        //going to have to double chjeck username not already associated,

        PreparedStatement createUser = null;
        Boolean result = false;

        String qStatement =         "insert into "
                + "users" + "(id, username, password, email, is_blocked, date_created, avatar, status) VALUES"
                + "(DEFAULT, ?, ?, ?, DEFAULT, ?, ?, DEFAULT);";

        try {
            createUser = con.prepareStatement(qStatement);

            createUser.setString(1, username);
            createUser.setString(2, password);
            createUser.setString(3, emailAddress);
            createUser.setTimestamp(4, getCurrentTimeStamp());
            createUser.setInt(5, avatar);

            int resultInt =  createUser.executeUpdate();

            if (resultInt == 1)
                result = true;

        } catch (SQLException e) {
            e.getMessage();

        } finally {
            if (createUser != null) {
                try {
                    createUser.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(result);
        return result;
    }





    public static User login(Connection con, String email) {
        /* a method to create a new user in database with provided emailAddress & password
         * return true if successful,
         * else false*/

        //gonna have to return current rs of users, so kn ow where to insert new
        //going to have to double chjeck username not already associated,


        User user = null;
        PreparedStatement retrieveUser = null;
        Boolean result = false;


        String qStatement =
                "select id, username, password, email, is_blocked, date_created, avatar, status " +
                        "from " +  "Users" +
                        " where email = ? ;";


        try {
            retrieveUser = con.prepareStatement(qStatement);

            retrieveUser.setString(1, email);
        //    retrieveUser.setString(2, password);
            ResultSet rs = retrieveUser.executeQuery();
            rs.next();
            int id = rs.getInt("id");
            String userN = rs.getString("username");
            Boolean is_blocked = rs.getBoolean("is_blocked");
            Date date = rs.getDate("date_created");
            int avatar = rs.getInt("avatar");
            String status = rs.getString("status");
            user = new User(id,  email, userN,  date, avatar, status);
        }

        catch (SQLException e) {
            e.getMessage();

        } finally {
            if (retrieveUser != null) {
                try {
                    retrieveUser .close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return user;
    }


    public static Boolean emailPresent(Connection con, String email) {

        PreparedStatement retrieveEmail = null;
        Boolean result = false;

        String qStatement =
                "select email " +
                        "from " +  "users" +
                        " where email = ?;";


        try {
            retrieveEmail = con.prepareStatement(qStatement);

            retrieveEmail.setString(1, email);
            ResultSet rs = retrieveEmail.executeQuery();

            if (rs.next() == true){
                result = true;
            };


        } catch (SQLException e) {
            e.getMessage();

        } finally {
            if (retrieveEmail != null) {
                try {
                    retrieveEmail.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    public static Boolean usernamePresent(Connection con, String username) {

        PreparedStatement retrieveUsername = null;
        Boolean result = false;

        String qStatement =
                "select username " +
                        "from " +  "users" +
                        " where username = ?;";


        try {
            retrieveUsername = con.prepareStatement(qStatement);

            retrieveUsername.setString(1, username);
            ResultSet rs = retrieveUsername.executeQuery();

            if (rs.next() == true){
                result = true;
            };


        } catch (SQLException e) {
            e.getMessage();

        } finally {
            if (retrieveUsername != null) {
                try {
                    retrieveUsername.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }


    public static String retrieveEmailLogin(Connection con, String email) {

        PreparedStatement retrieveEmail = null;


        String qStatement =
                "select email " +
                        "from " +  "users" +
                        " where email = ?";


        try {
            retrieveEmail = con.prepareStatement(qStatement);

            retrieveEmail.setString(1, email);
            ResultSet rs = retrieveEmail.executeQuery();
            rs.next();
            email = rs.getString("email");

        } catch (SQLException e) {
            e.getMessage();

        } finally {
            if (retrieveEmail != null) {
                try {
                    retrieveEmail.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return email;
    }

    public static int retrieveID(Connection con, User user)

            throws SQLException {

        String username = user.getUsername();
        int id = 0;
        PreparedStatement retrieveID = null;


        String qStatement =
                "select id " +
                        "from " + "users" +
                        " where username = ?";


        try {
            retrieveID = con.prepareStatement(qStatement);

            retrieveID.setString(1, username);
            ResultSet rs = retrieveID.executeQuery();
            rs.next();
            username = rs.getString("id");


        } catch (SQLException e) {
            e.getMessage();

        } finally {
            if (retrieveID != null) {
                retrieveID.close();
            }

        }
        return id;
    }


    public static int retrieveIDEmail(Connection con, String userEmail) {

        int id = 0;
        PreparedStatement retrieveID = null;


        String qStatement =
                "select id " +
                        "from " + "users" +
                        " where email = ?";


        try {
            retrieveID = con.prepareStatement(qStatement);

            retrieveID.setString(1, userEmail);
            ResultSet rs = retrieveID.executeQuery();
            rs.next();
            id = rs.getInt("id");


        } catch (SQLException e) {
            e.getMessage();

        } finally {
            if (retrieveID != null) {
                try {
                    retrieveID.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

        return id;
    }

    // a method to connect to database and store password provided as arg for user provided as arg,
    // return true if successful, else false*/



    public static boolean setPassword(Connection con, String userEmail, String password) {

        //int id = user.getID()
        int id = retrieveIDEmail(con, userEmail);
        Boolean result = false;

        PreparedStatement setPassword = null;

        String qStatement =
                "UPDATE "
                        + "users" + " set password = ? where id = ?";

        try {
            setPassword = con.prepareStatement(qStatement);

            setPassword.setString(1, password);
            setPassword.setInt(2, id);
            int resultInt = setPassword.executeUpdate();
            if (resultInt == 1)
                result = true;

        } catch (SQLException e) {
            e.getMessage();

        } finally {
            if (setPassword != null) {
                try {
                    setPassword.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }


        }
        return result;
    }

    public static boolean setStatus(Connection con, String userEmail, String status) {

        int id = retrieveIDEmail(con, userEmail);
        Boolean result = false;

        PreparedStatement setStatus = null;

        String qStatement =
                "UPDATE "
                        + "users" + " set status = ? where id = ?";

        try {
            setStatus = con.prepareStatement(qStatement);

            setStatus.setString(1, status);
            setStatus.setInt(2, id);
            int resultInt = setStatus.executeUpdate();
            if (resultInt == 1) {
                System.out.println("Reaching code block!!");
                result = true;
            }

        } catch (SQLException e) {
            e.getMessage();

        } finally {
            if (setStatus != null) {
                try {
                    setStatus.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }


        }
        return result;
    }

    //  a method to connect to database and store password provided as arg for user provided as arg,
    //  return true if successful, else false*/


    public static boolean checkCredentials(Connection con, String userEmail, String password) {

	   /*a method to check DB to ensure user email and password combination exists / is correct
       if both are correct return true
       otherwise return false
       NOTE - could change so that we return int value which indicates whether user email or pw is correct
       / incorrect though not strictly necessary / many other apps do not do this - LESS SECURE*/

        //int id = user.getID();

        Boolean result = false;
        int id = retrieveIDEmail(con, userEmail);
        PreparedStatement checkCreds = null;
        String email = "";
        String passwordInDB = "";

        String qStatement =
        "select email, password " +
                "from " + "users" +
                " where id = ?";


        try {
            checkCreds = con.prepareStatement(qStatement);

            checkCreds.setInt(1, id);
            ResultSet rs = checkCreds.executeQuery();
            rs.next();
            email = rs.getString("email");
            passwordInDB = rs.getString("password");



        } catch (SQLException e) {
            e.getMessage();
            e.printStackTrace();

        } finally {
            if (checkCreds != null) {
                try {
                    checkCreds.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        if ((email.equals(userEmail)) && (BCrypt.checkpw(password, passwordInDB)))
            result = true;
        return result;

    }


    public static java.sql.Timestamp getCurrentTimeStamp(){
        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());
    }

    // if i had this as recipient \ID makes it group applicable?
    public static Conversation startConversation(Connection con, User user, User recipient) {

        int userID = user.getID();
        int recipientID = recipient.getID();

        ArrayList<UserMessage> messages = null;
        ArrayList<User> participants = null;
        Conversation convo = null;

        PreparedStatement newConvo = null;
        PreparedStatement newUserConvo = null;
        PreparedStatement newRecipientConvo = null;

        String newConversation = "INSERT INTO conversation (id, created_at, updated_at) " +
                "VALUES(DEFAULT, ?, ?);";

        String newUserConvoString = "insert into "
                + "user_conversation " + "(id, conversation_id, user_id) VALUES"
                + "(DEFAULT, ?, ?), (DEFAULT, ?, ?);";

        try {
            con.setAutoCommit(false);

            newConvo = con.prepareStatement(newConversation, Statement.RETURN_GENERATED_KEYS);
            newUserConvo = con.prepareStatement(newUserConvoString);

            newConvo.setTimestamp(1, getCurrentTimeStamp());
            newConvo.setTimestamp(2, getCurrentTimeStamp());

            newConvo.executeUpdate();

            ResultSet rs = newConvo.getGeneratedKeys();

            if (rs.next()) {
                long key = rs.getLong(1);
            }

            newUserConvo.setInt(1, rs.getInt(1));
            newUserConvo.setInt(2, userID);
            newUserConvo.setInt(3, rs.getInt(1));
            newUserConvo.setInt(4, recipientID);
            newUserConvo.executeUpdate();

            con.commit();


            messages = retrieveMessages(con, rs.getInt(1));
            if (messages == null) {
                messages = new ArrayList<UserMessage>();
            }
            participants = retrieveParticipants(con, rs.getInt(1));
            convo = new Conversation(rs.getInt(1), participants, messages);
            con.setAutoCommit(true);

        } catch (SQLException e ) {
            e.getMessage();
            if (con != null) {
                try {
                    System.err.print("Message not stored in database");
                    con.rollback();
                } catch(SQLException excep) {
                    e.getMessage();
                }
            }

        } finally {
            if (newConvo != null) {
                try {
                    newConvo.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (newUserConvo != null) {
                try {
                    newUserConvo.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (newRecipientConvo != null) {
                try {
                    newRecipientConvo.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }


        }
        return convo;


    }

    // just get int id for recipient to make group appropriate
    public static Conversation retrieveConversation(Connection con, User user, User recipient) {

        int convoID = 0;
        int userID = user.getID();
        int recipientID = recipient.getID();
        ArrayList<UserMessage> userMessages = new ArrayList<UserMessage>();
        ArrayList<User> participants = new ArrayList<User>();
        Conversation convo = new Conversation(0, participants, userMessages);

        PreparedStatement retrieveUserConversations = null;
        PreparedStatement retrieveRecipientConversations = null;
        String retrieveConversationString =  "select conversation_id " +
                "from " +  "user_conversation" +
                " where user_id = ?;";

        try {
            retrieveUserConversations = con.prepareStatement(retrieveConversationString);
            retrieveUserConversations.setInt(1, userID);
            ResultSet rs = retrieveUserConversations.executeQuery();

            ArrayList<Integer> al1 = new ArrayList<Integer>();
            while (rs.next()) {
                al1.add(rs.getInt("conversation_id"));;
            }
            rs.close();

            retrieveRecipientConversations = con.prepareStatement(retrieveConversationString);
            retrieveRecipientConversations.setInt(1, recipientID);
            ResultSet rs2 = retrieveRecipientConversations.executeQuery();

            ArrayList<Integer> al2 = new ArrayList<Integer>();
            while (rs2.next()) {
                al2.add(rs2.getInt("conversation_id"));
            }
            rs2.close();

            if (!al1.isEmpty() || !al2.isEmpty()) {
                for (Integer a : al1) {
                    for (Integer b : al2) {
                        if (a.equals(b)) {
                            convoID = a;
                            break;
                        }
                    }
                }
            }

            if(convoID == 0){
                convo = startConversation(con, user, recipient);
            } else {
                userMessages = retrieveMessages(con, convoID);
                participants = retrieveParticipants(con, convoID);
                convo = new Conversation(convoID, participants, userMessages);
            }
        }catch (SQLException e) {
            e.getMessage();

        } finally {
            if (retrieveUserConversations != null) {
                try {
                    retrieveUserConversations.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (retrieveRecipientConversations != null) {
                try {
                    retrieveUserConversations.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return convo;
    }


    public static ArrayList<UserMessage> retrieveMessages(Connection con, int convoID)  {


        ArrayList<UserMessage> userMessages = new ArrayList<UserMessage>();

        PreparedStatement messageLog = null;
        String retrieveMessages =  "select sender_id, sent_at, conversation_id, message_text " +
                "from " +  "messages" +
                " where conversation_id = ?;";

        try {
            messageLog = con.prepareStatement(retrieveMessages);
            messageLog.setInt(1, convoID);
            ResultSet rs = messageLog.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("sender_id");
                java.sql.Timestamp date = rs.getTimestamp("sent_at");
                int conversationID = rs.getInt("conversation_id");
                String messageText = rs.getString("message_text");
                User returnUser = retrieveUser(con, id);

                UserMessage message = new UserMessage(returnUser, date, conversationID, messageText);
                userMessages.add(message);
            }
            rs.close();

        }catch (SQLException e) {
            e.getMessage();
            e.printStackTrace();

        } finally {
            if (messageLog != null) {
                try {
                    messageLog.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return userMessages;

    }

    public static User retrieveUser(Connection con, int id) {

        User user = null;
        PreparedStatement retrieveUser = null;
        Boolean result = false;


        String qStatement =
                "select id, username, password, email, is_blocked, date_created, avatar, status " +
                        "from " +  "Users" +
                        " where id = ? ;";

        try {
            retrieveUser = con.prepareStatement(qStatement);

            retrieveUser.setInt(1, id);
            ResultSet rs = retrieveUser.executeQuery();
            rs.next();
            //int id = rs.getInt("id");
            String userN = rs.getString("username");
            String email = rs.getString("email");
            Boolean is_blocked = rs.getBoolean("is_blocked");
            Date date = rs.getDate("date_created");
            int avatar = rs.getInt("avatar");
            String status = rs.getString("status");
            user = new User(id,  email, userN,  date, avatar, status);
        }

        catch (SQLException e) {
            e.getMessage();

        } finally {
            if (retrieveUser != null) {
                try {
                    retrieveUser .close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return user;
    }



    // retrieve convo user and user
    // check if convo present bool take two users
    public static Boolean conversationPresent(Connection con, User user, User recipient)  {

        int userID = user.getID();
        int recipientID = recipient.getID();
        Boolean convoPresent = false;

        PreparedStatement retrieveUserConversations = null;
        PreparedStatement retrieveRecipientConversations = null;
        String retrieveConversationString =  "select conversation_id " +
                "from " +  "user_conversation" +
                " where user_id = ?;";

        try {
            retrieveUserConversations = con.prepareStatement(retrieveConversationString);
            retrieveUserConversations.setInt(1, userID);
            ResultSet rs = retrieveUserConversations.executeQuery();

            ArrayList<Integer> al1 = new ArrayList<Integer>();
            while (rs.next()) {
                al1.add(rs.getInt("conversation_id"));
                int convoID = rs.getInt("conversation_id");
            }
            rs.close();

            retrieveRecipientConversations = con.prepareStatement(retrieveConversationString);
            retrieveRecipientConversations.setInt(1, recipientID);
            ResultSet rs2 = retrieveRecipientConversations.executeQuery();

            ArrayList<Integer> al2 = new ArrayList<Integer>();
            while (rs2.next()) {
                al2.add(rs2.getInt("conversation_id"));
                int convoID = rs2.getInt("conversation_id");
            }
            rs2.close();

            if (!al1.isEmpty() || !al2.isEmpty()) {
                for (Integer a : al1) {
                    for (Integer b : al2) {
                        if (a.equals(b)) {
                            convoPresent = true;
                            break;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            e.getMessage();


        } finally {
            if (retrieveUserConversations != null) {
                try {
                    retrieveUserConversations.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (retrieveRecipientConversations != null) {
                try {
                    retrieveRecipientConversations.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return convoPresent;
    }


    public static ArrayList<User> retrieveParticipants(Connection con, int convoID) throws SQLException {

        ArrayList<User> participants = new ArrayList<User>();
        PreparedStatement retrieveParticipants = null;

        String retrieveContactString = "select users.id, username, email, date_created, avatar, status from users\n" +
                "  join user_conversation on users.id = user_conversation.user_id\n" +
                "where conversation_id = ?;";

        try {
            retrieveParticipants = con.prepareStatement(retrieveContactString);
            retrieveParticipants.setInt(1, convoID);
            ResultSet rs = retrieveParticipants.executeQuery();

            while(rs.next()){
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                Date date = rs.getDate("date_created");
                int avatar = rs.getInt("avatar");
                String status = rs.getString("status");
                User user = new User(id, email, username, date, avatar, status);
                participants.add(user);
            }
        }catch (SQLException e) {
            e.getMessage();

        } finally {
            if (retrieveParticipants != null) {
                retrieveParticipants.close();
            }

        }
        return participants;

    }

    public static UserMessage newMessage(Connection con, User sender, User recipient, int convoID, String messageText) {

        int userID = sender.getID();
        UserMessage message = null;

        PreparedStatement storeNewMessage = null;
        PreparedStatement updateConvo = null;

        //IGNORING DUE TO TIME, THIS WOULD BE TO STORE NUMBER OF TIMES CONTACTS HAVE CONTACTED EACH
        //OTHER, WOULD NEED TO STORE TWO RECORDS FOR EACH INTERACTION! OTHERWISE UNNECESSARY PROCESSING
        //WOULD BE REQUIRED TO CALCULATE IF SENDER IS THE 'CONTACT_ID' OR 'USER_ID'
//        PreparedStatement updateUserContacts = null;
//        PreparedStatement retrieveUserContacts  = null;


        //TO STORE THE MESSAGE!!
        String storeNewMessageEntryString =
                "insert into "
                        + "messages" + "(id, sender_id, conversation_id, message_text, sent_at) VALUES"
                        + "(DEFAULT, ?, ?, ?, ?);";

        //TO UPDATE THE LAST TIME A MESSAGE WAS ADDED TO A CONVERSATION
        String updateConvoString = "UPDATE "
                + "conversation" + " set updated_at = ? where id = ?;";

        //IGNORING DUE TO TIME, THIS WOULD BE TO STORE NUMBER OF TIMES CONTACTS HAVE CONTACTED EACH
        //OTHER, WOULD NEED TO STORE TWO RECORDS FOR EACH INTERACTION! OTHERWISE UNNECESSARY PROCESSING
        //WOULD BE REQUIRED TO CALCULATE IF SENDER IS THE 'CONTACT_ID' OR 'USER_ID'
        //TO GET NUMBER OF TIMES CONTACTS HAVE MESSAGED EACH OTHER
//        String retrieveUserContactsString = "select times_contacted "
//                + "from user_contact" + " where contact_id = ? and user_id = ?;";

        //TO UPDATE NUMBER OF TIMES CONTACTS HAVE MESSAGED EACH OTHER
        //IGNORING DUE TO TIME, THIS WOULD BE TO STORE NUMBER OF TIMES CONTACTS HAVE CONTACTED EACH
        //OTHER, WOULD NEED TO STORE TWO RECORDS FOR EACH INTERACTION! OTHERWISE UNNECESSARY PROCESSING
        //WOULD BE REQUIRED TO CALCULATE IF SENDER IS THE 'CONTACT_ID' OR 'USER_ID'
        //TO GET NUMBER OF TIMES CONTACTS HAVE MESSAGED EACH OTHER
//        String retrieveUserContactsString = "select times_contacted "
//                + "from user_contact" + " where contact_id = ? and user_id = ?;";


        try {
            con.setAutoCommit(false);
            storeNewMessage = con.prepareStatement(storeNewMessageEntryString);
            storeNewMessage.setInt(1, userID);
            storeNewMessage.setInt(2, convoID);
            storeNewMessage.setString(3, messageText);
            storeNewMessage.setTimestamp(4, getCurrentTimeStamp());
            storeNewMessage.executeUpdate();

            updateConvo = con.prepareStatement(updateConvoString);
            updateConvo.setTimestamp(1, getCurrentTimeStamp());
            updateConvo.setInt(2, convoID);
            updateConvo.executeUpdate();

            //IGNORING DUE TO TIME, THIS WOULD BE TO STORE NUMBER OF TIMES CONTACTS HAVE CONTACTED EACH
            //OTHER, WOULD NEED TO STORE TWO RECORDS FOR EACH INTERACTION! OTHERWISE UNNECESSARY PROCESSING
            //WOULD BE REQUIRED TO CALCULATE IF SENDER IS THE 'CONTACT_ID' OR 'USER_ID'
//            retrieveUserContacts = con.prepareStatement(retrieveUserContactsString);
//            retrieveUserContacts.setInt(1, recipientID);
//            retrieveUserContacts.setInt(2, userID);

//            updateUserContacts = con.prepareStatement(updateContactsString);
//            updateUserContacts.setInt(3, id);

//
//            int participantsID = participants.get(i).getID();
//
//            retrieveUserContacts.setInt(1, participantsID);
//            ResultSet rs = retrieveUserContacts.executeQuery();
//            rs.next();
//            int userAsUser = rs.getInt("times_contacted");
//            userAsUser++;
//
//            retrieveContactsInteractionWithUser.setInt(2, participantsID);
//            ResultSet rs2 = retrieveContactsInteractionWithUser.executeQuery();
//            rs2.next();
//            int userAsContact = rs2.getInt("times_contacted");
//            userAsContact++;
//
//            updateUserContacts.setInt(1, userAsUser);
//            updateUserContacts.setInt(2, participantsID);
//            updateUserContacts.executeUpdate();
//
//            updateContactsInteractionWithUser.setInt(1, userAsContact);
//            updateContactsInteractionWithUser.setInt(3, participantsID);
//            updateContactsInteractionWithUser.executeUpdate();
//
//            User returnUser = retrieveUser(con, id);

            con.commit();
            message = new UserMessage(sender, getCurrentTimeStamp(),convoID, messageText);
        } catch (SQLException e ) {
            e.getMessage();
            if (con != null) {
                try {
                    System.err.print("Message not stored in database");
                    con.rollback();
                } catch(SQLException excep) {
                    e.getMessage();
                }
            }
        } finally {
            if (storeNewMessage != null) {
                try {
                    storeNewMessage.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (updateConvo != null) {
                try {
                    updateConvo.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
//            if (retrieveUserContacts != null) {
//                try {
//                    retrieveUserContacts.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (updateUserContacts != null) {
//                try {
//                    updateUserContacts.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (retrieveContactsInteractionWithUser != null) {
//                try {
//                    retrieveContactsInteractionWithUser.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (updateContactsInteractionWithUser != null) {
//                try {
//                    updateContactsInteractionWithUser.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }

            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return message;
    }


}

