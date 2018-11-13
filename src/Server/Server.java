package Server;

import ClientServerMessages.*;
import Email.Email;
import org.mindrot.jbcrypt.BCrypt;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Server class which provides the core functionality for the Server-side OIOIHanoi application.
 * The class contains a main method which will start an instance of the OIOIHanoi server. An object of Server
 * can also be constructed and used elsewhere (specifically the ServerLauncher class which provides a GUI for running
 * an OIOIHanoi server. The Server requires a host, port and backlog and upon start will create a ServerSocket which is used
 * to listen for incoming Client connections. The process of accepting connections is handled in the ClientRequestHandler
 * class which operates on a separate thread from the Server itself. The Server also creates and holds on to a Database connection
 * and a pool of ClientThreads (which are created by the ClientRequestHandler upon successful receipt of a Client request to connect.
 * @author lxf736
 * @version 2018-03-01
 */

public class Server {

    private int port;
    private String host;
    private int backlog;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private Map<User, ClientThread> onlineUsers;
    private boolean serverRunning;
    private Connection databaseConnection;
    private ClientRequestHandler clientRequestHandler;

    /**
     * Constructs an instance of Server. Taking a String host, int port and int backlog as params
     * @param host - the name of the host machine the Server is to run on as a String
     * @param port - the port which will be used to create a ServerSocket (which will listen for incoming
     *             client requests) as an int
     * @param backlog - the maximum number of connection requests the Server will queue up as an int
     */
    public Server(String host, int port, int backlog) {
        this.port = port;
        this.host = host;
        this.backlog = backlog;
        this.threadPool = Executors.newCachedThreadPool();
        this.onlineUsers = new HashMap<User, ClientThread>();
        try {
            this.serverSocket = new ServerSocket();
        } catch (IOException e) {

        }
    }

    /**
     * a main method which creates an object of Server by taking two params, String host arg[1] and int port arg[2]
     * (backlog is defaulted to 100). The server is then started using the values proved as args.
     * @param args
     */
    public static void main(String[] args) {
        Server server = new Server(args[0], Integer.parseInt(args[1]), 100);
        try {
            server.start();
        } catch (IOException e) {
           server.stopServer();
            e.printStackTrace();
            System.out.println("Server could not be started with provided args! Please ensure args are correct.");
        } catch (SQLException e) {
            System.out.println("could not establish Database connection ");
            e.printStackTrace();
            server.stopServer();
        }
    }

    /**
     * a method to start an object of type Server. This method will set up all required connections and dependencies
     * (i.e Database connection, ServerSocket, ClientRequestHandler).
     * @throws IOException if a ServerSocket cannot be created using the values assigned to the Server at instantiation.
     * @throws SQLException if a Database connection cannot be established
     */
    public void start() throws IOException, SQLException {
        this.serverSocket = new ServerSocket(this.port, this.backlog, (InetAddress.getByName(this.host)));
        System.out.println("Server Started at: " + this.serverSocket);
        if (this.serverSocket != null) {
            this.databaseConnection = Database.connectToDatabase();
            serverRunning = true;
            clientRequestHandler = new ClientRequestHandler(this, threadPool, serverSocket);
            this.clientRequestHandler.start();
        }
    }

    /**
     * a method to add a user to the Servers onlineUsers Map. This is called upon a successful Client signIn().
     * This method then calls updateOnlineUser() for each onlineUser which will inform all online Users that a
     * new User has signed in.
     * @param newUser the User to be added to the onlineUsers Map
     * @param ct the ClientThread responsible for the newUsers communication.
     */
    public synchronized void addOnlineUser(User newUser, ClientThread ct) {
        onlineUsers.put(newUser, ct);
        onlineUsers.values().forEach(e -> {
            try {
                e.updateOnlineUsers(newUser, true);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    /**
     * a method to remove a user from the Servers onlineUsers Map.
     * This method then calls updateOnlineUsers() which will inform all online Users that the User has signed out.
     * @param removeUser the User to be removed from the onlineUsers Map
     */
    public synchronized void signOutUser(User removeUser) {
        onlineUsers.remove(removeUser);
        onlineUsers.values().forEach(e -> {
            try {
                e.updateOnlineUsers(removeUser, false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    /**
     * a method to convert the keyset values of onlineUsers Map to an ArrayList<User>
     * @return an ArrayList<User> of all online Users.
     */
    public ArrayList<User> convertOnlineUsersToList () {
        ArrayList<User> result = new ArrayList<User>();
        this.onlineUsers.keySet().forEach(e-> result.add(e));
        return result;
    }

    /**
     * a method to indicate whether the instantiated object of Server is currently running
     */
    public boolean getServerRunning() {
        return this.serverRunning;
    }

    /**
     * a method to sign up a new User. This method is called upon receipt of a SignUpMessage from a Client.
     * The method undertakes a number of checks to ensure that the request is valid (i.e. their is not already a User stored
     * in the database with the requested credentials). If the request is valid the User is created in the Database, an email is
     * sent to the provided email address confirming sign up and a successful response is recorded on the SignUpMessage before being
     * written back to the requesting Client. Else an appropriate unsuccessful response is recorded on the SignUpMessage and sent
     * back to the requesting Client.
     * @param message a SignUpMessage sent from a Client
     * @param thread the ClientThread that received the message / called this method.
     * @throws IOException
     */
    public void signUp(SignUpMessage message, ClientThread thread) throws IOException {
        if (Database.emailPresent(databaseConnection, message.getEmail()) || Database.usernamePresent(databaseConnection, message.getUsername())) {
            message.setResponse("User already exists, please try signing in", false);
            System.out.println(thread.constructSendingThread(thread) + message.getResponseMessage());
            thread.getToClient().writeObject(message);
        } else if (!validateEmail(message.getEmail())) {
            message.setResponse("Email Address must be in valid format", false);
            System.out.println(thread.constructSendingThread(thread) + message.getResponseMessage());
            thread.getToClient().writeObject(message);
        } else if (!validateAlphanumeric(message.getUsername())) {
            message.setResponse("Username Containing A-Z 0-9 Only", false);
            System.out.println(thread.constructSendingThread(thread) + message.getResponseMessage());
            thread.getToClient().writeObject(message);
        } else if (message.getUsername().length() < 5 || message.getUsername().length() > 20) {
            message.setResponse("Username must be between 5 and 20 characters in length", false);
            System.out.println(thread.constructSendingThread(thread) + message.getResponseMessage());
            thread.getToClient().writeObject(message);
        } else if (message.getPassword().length() < 8 || message.getPassword().length() >100) {
            message.setResponse("Password must be between 8 and 20 characters in length", false);
            System.out.println(thread.constructSendingThread(thread) + message.getResponseMessage());
            thread.getToClient().writeObject(message);
        } else if (Database.createUser(databaseConnection, message.getUsername(), message.getPassword(), message.getEmail(), message.getAvatar())){
            message.setResponse("Sign up successful", true);
            System.out.println(thread.constructSendingThread(thread) + message.getResponseMessage());
            thread.getToClient().writeObject(message);
            thread.stopThread();
            ArrayList<String> email = new ArrayList<String>();
            email.add(message.getEmail());
            try {
                Email.sendEmail(email, "Welcome to OIOIHanoi!", "Hi " + message.getUsername() +
                    " thanks for choosing to use our messaging platform! We hope you enjoy using our product." );
            }
        }
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
    public static final Pattern VALID_ALPHANUMERIC_REGEX =
        Pattern.compile("^[a-zA-Z0-9_+-]*$", Pattern.CASE_INSENSITIVE);

    public static boolean validateAlphanumeric(String alpha) {
        Matcher matcher = VALID_ALPHANUMERIC_REGEX.matcher(alpha);
        return matcher.find();
    }


    /**
     * a method to signIn a User. This method is called upon receipt of a SignInMessage from a Client.
     * The method undertakes a number of checks to ensure that the request is valid (i.e. the User is not alread logged in, their credentials
     * match those stored in the Database). If the request is valid a successful response is recorded on the SignInMessage and a User object for the
     * requesting User is also set on the SignInMessage to be used by the Client. Else an appropriate unsuccessful response is recorded on the
     * SignInMessage and sent
     * back to the requesting Client.
     * @param message a SignUpMessage sent from a Client
     * @param thread the ClientThread that received the message / called this method.
     * @throws IOException
     */
    public synchronized void signIn(SignInMessage message, ClientThread thread) throws IOException {
        if (checkEmailLocally(message.getEmail())) {
            message.setResponse(message.getEmail() + " is already logged in!!", false);
            System.out.println(thread.constructSendingThread(thread) + message.getResponseMessage());
            thread.getToClient().writeObject(message);
        } else if (Database.checkCredentials(databaseConnection, message.getEmail(), message.getPassword())) {
            thread.setUser(Database.login(databaseConnection, message.getEmail()));
            message.setResponse(thread.getUser().getUsername() + " has successfully logged in", true, thread.getUser(), convertOnlineUsersToList());
            System.out.println(thread.constructSendingThread(thread) + message.getResponseMessage());
            thread.getToClient().writeObject(message);
            addOnlineUser(thread.getUser(), thread);
        } else {
            message.setResponse( "No user found with provided details", false);
            System.out.println(thread.constructSendingThread(thread) + message.getResponseMessage());
            thread.getToClient().writeObject(message);
        }
    }

    public synchronized void newConversation (StartConversationMessage message, ClientThread thread) throws IOException {
        if (Database.conversationPresent(databaseConnection, message.getSender(), message.getRecipient())) {
            Conversation conversation = Database.retrieveConversation(databaseConnection, message.getSender(), message.getRecipient());
            message.setResponse("existing conversation between " +
                message.getSender().getUsername() + " and " + message.getRecipient().getUsername(), true, conversation);
            ClientThread threadRecipient = getUserThreadFromMap(message.getRecipient());
            thread.getToClient().writeObject(message);
            threadRecipient.getToClient().writeObject(message);
            System.out.println(thread.constructSendingThread(thread) + message.getResponseMessage());
            System.out.println(threadRecipient.constructSendingThread(thread) + message.getResponseMessage());
        } else {
            Conversation conversation = Database.startConversation(databaseConnection, message.getSender(), message.getRecipient());
            message.setResponse("new conversation between " +
                message.getSender().getUsername() + " and " + message.getRecipient().getUsername(), true, conversation);
            ClientThread threadRecipient = getUserThreadFromMap(message.getRecipient());
            thread.getToClient().writeObject(message);
            threadRecipient.getToClient().writeObject(message);
            System.out.println(thread.constructSendingThread(thread) + message.getResponseMessage());
            System.out.println(threadRecipient.constructSendingThread(thread) + message.getResponseMessage());
        }
    }

    public synchronized void newMessage(MessagingMessage message, ClientThread thread) throws IOException {
        message.getRecipient().getUsername();
        UserMessage userMessage = Database.newMessage(databaseConnection, message.getSender(), message.getRecipient(), message.getConversationID(), message.getMessageText());
        Conversation conversation = Database.retrieveConversation(databaseConnection, message.getSender(), message.getRecipient());
        message.setResponse("sending conversation back with latest message", true, conversation);
        ClientThread threadRecipient = getUserThreadFromMap(message.getRecipient());
        thread.getToClient().writeObject(message);
        threadRecipient.getToClient().writeObject(message);
        System.out.println(thread.constructSendingThread(thread) + message.getResponseMessage());
        System.out.println(threadRecipient.constructSendingThread(thread) + message.getResponseMessage());
    }

    public void resetPassword(ResetPasswordMessage message, ClientThread thread) throws IOException {
        boolean emailSent = false;
        Random rnd = new Random();
        int pw = 100000 + rnd.nextInt(900000);
        String tempPass = Integer.toString(pw);
        String encryptedPass = BCrypt.hashpw(tempPass, BCrypt.gensalt());
        if (Database.setPassword(databaseConnection, message.getRequestingUser(),encryptedPass)) {
            ArrayList<String> to = new ArrayList<String>();
            to.add(message.getRequestingUser());
            try {
                Email.sendEmail(to, "OiOiHanoi Password Reset", "Please return to OiOiHanoi and enter the below number as prompted: \n " +
                        tempPass);
                emailSent = true;
            } catch (IOException e) {
                System.out.println("password reset failed to send to " + message.getRequestingUser());
            }
        }
        if (emailSent) {
            message.setResponse("password reset email sent to " + message.getRequestingUser(), true);
        } else {
            message.setResponse("No account found with this email address", false);
        }
        System.out.println(thread.constructSendingThread(thread) + message.getResponseMessage());
        thread.getToClient().writeObject(message);
    }

    public void changePassword(ChangePasswordMessage message, ClientThread thread) throws IOException {
        if (Database.checkCredentials(databaseConnection, message.getRequestingUser(), message.getCode())) {
            String encryptedPass = BCrypt.hashpw(message.getNewPassword(), BCrypt.gensalt());
            Database.setPassword(databaseConnection, message.getRequestingUser(), encryptedPass);
            message.setResponse("Password successfully changed, please log in", true);
        } else {
            message.setResponse("Please ensure reset code was entered correctly", false);
        }
        System.out.println(thread.constructSendingThread(thread) + message.getResponseMessage());
        thread.getToClient().writeObject(message);
    }

    public void updateUserStatus(UpdateStatusMessage message, ClientThread thread) {
        if(Database.setStatus(databaseConnection, message.getUser().getEmail(), message.getStatus())){
            message.setResponse("status successfully updated", true);
            onlineUsers.values().forEach(e -> {
                try {
                    e.updateUserStatus(message);
                    System.out.println(thread.constructSendingThread(thread) + message.getResponseMessage());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
        } else {
            message.setResponse("status not changed", false);
            try {
                thread.getToClient().writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void stopServer() {
        this.finalize();
    }

    @Override
    public void finalize() {
        serverRunning = false;
        try {
            serverSocket.close();
            System.out.println("Server socket was closed successfully");
        } catch (IOException e) {
            System.out.println("Server socket could not be closed!");
        }
        if (clientRequestHandler != null) {
            clientRequestHandler.interrupt();
        }
        threadPool.shutdown();
        System.out.println("Worker threads terminated");
        try {
            if(databaseConnection != null) {
                databaseConnection.close();
                System.out.println("Database connection successfully closed");
            }
        } catch (SQLException e) {
            System.out.print("Database connection not closed!");
        }
        System.exit(0);
    }

    public ClientThread getUserThreadFromMap (User user) {
        User key = user;
        ArrayList<User> userArrayList = new ArrayList<User>();
        this.onlineUsers.keySet().forEach(e -> userArrayList.add(e));
        for (User e : userArrayList) {
            if (e.getUsername().equals(user.getUsername())) {
                key = e;
            }
        }
        ClientThread thread = this.onlineUsers.get(key);
        return thread;
    }

    public Boolean checkEmailLocally (String email) {
        boolean result = false;
        ArrayList<User> userArrayList = new ArrayList<User>();
        this.onlineUsers.keySet().forEach(e -> userArrayList.add(e));
        for (User e : userArrayList) {
            if (e.getEmail().equals(email)) {
                result = true;
            }
        }
        return result;
    }

}
