package Client;

import ClientServerMessages.*;

import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author lxf736
 * @version 2018-03-15
 */

public class Client {

    private String host;
    private Integer port;
    private Socket clientSocket;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    private User user;
    private String currentlySelectedUser;
    private String lastReceivedUser;
    private ClientListener listener;
    private ArrayList<User> onlineUsers;
    private ArrayList<String> userNotifications = new ArrayList<String>();
    private Map<String, Conversation> conversationsString = new HashMap<>();
    private final List<ConversationListener> conversationListeners = new ArrayList<>();
    private final List<NewMessageListener> messageListeners = new ArrayList<>();
    private final List<UserListener> userListeners = new ArrayList<>();
    private final List<AvailabilityListener> availabilityListeners = new ArrayList<>();
    private final List<StatusListener> statusListeners = new ArrayList<>();


    public Client(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        clientSocket = new Socket(this.host, this.port);
        toServer = new ObjectOutputStream(clientSocket.getOutputStream());
        fromServer = new ObjectInputStream(clientSocket.getInputStream());
        this.currentlySelectedUser = "!";
        this.lastReceivedUser = "";
    }

    public void startListener() {
        listener= new ClientListener(this, this.clientSocket, this.toServer, this.fromServer);
        listener.start();
    }

    public String getLastReceivedUser() {
        return lastReceivedUser;
    }

    public void stopListener() {
        listener.interrupt();
    }

    public ArrayList<String> getNotifications() {
        return this.userNotifications;
    }

    public ClientListener getListener() {
        return listener;
    }

    public String getLastReceivedMessage () {
        return lastReceivedUser;
    }

    public String getCurrentlySelectedUser() {
        return currentlySelectedUser;
    }

    public void setCurrentlySelectedUser(String currentlySelectedUser) {
        this.currentlySelectedUser = currentlySelectedUser;
    }

    public Message interpretMessage() throws IOException {
        Message message = null;
        try {
            message = (Message)fromServer.readObject();
        } catch (ClassNotFoundException e) {
            System.out.println("The returned UserMessage type was not recognised! Please ensure it has been imported");
        }
        if (message != null) {
            return message;
        } else {
            return null;
        }
    }

    public Message writeMessageToServer(Message message) {
        try {
            toServer.writeObject(message);
            Message response = this.interpretMessage();
            return response;
        } catch (IOException e) {
            for(AvailabilityListener availabilityListener : availabilityListeners) {
                availabilityListener.serverAvailable();
            }
            return null;
        }
    }

    public void writeMessageToServerNoResponse (Message message) {
        try {
            toServer.writeObject(message);
        } catch (IOException e) {
            for(AvailabilityListener availabilityListener : availabilityListeners) {
                availabilityListener.serverAvailable();
            }
        }
    }


    public SignUpMessage signUp(String emailAddress, String username, String password, int avatar){
        Message message = new SignUpMessage(emailAddress, username, password, avatar);
        return (SignUpMessage)writeMessageToServer(message);
    }

    public SignInMessage signIn(String username, String pass){
        Message message = new SignInMessage(username, pass);
        return (SignInMessage)writeMessageToServer(message);
    }

    public ResetPasswordMessage resetPassword(String requestingUserEmail) {
        Message message = new ResetPasswordMessage(requestingUserEmail);
        return (ResetPasswordMessage) writeMessageToServer(message);
    }

    public ChangePasswordMessage changePassword(String requestingUserEmail, String code, String newPassword) {
        Message message = new ChangePasswordMessage(requestingUserEmail, code, newPassword);
        return (ChangePasswordMessage) writeMessageToServer(message);
    }

    public User getUser () {
        return this.user;
    }

    public void setUser (User user) {
        this.user = user;
    }

    public ArrayList<User> getOnlineUsers() {
        return this.onlineUsers;
    }

    public void updateOnlineUsers(UpdateOnlineUsersMessage message) {
        if (message.getLogin()) {
            onlineUsers.add(message.getUser());
            for(UserListener userListener : userListeners) {
                userListener.userChange();
            }
        } else {
            onlineUsers.remove(message.getUser());
            for(UserListener userListener : userListeners) {
                userListener.userChange();
            }
        }
    }

    public synchronized void setOnlineUsers(ArrayList<User> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    public boolean checkConversation(User recipient) {
        if (this.conversationsString.containsKey(recipient.getUsername())) {
            return true;
        } else {
            return false;
        }
    }

    public synchronized Conversation getConversation (String recipient) {
        return conversationsString.get(recipient);
    }

    public void updateConversationsList(StartConversationMessage message) {
        if (message.getRecipient().equals(this.user)) {
            this.conversationsString.put(message.getSender().getUsername(), message.getConversation());
        } else {
            this.conversationsString.put(message.getRecipient().getUsername(), message.getConversation());
            for(ConversationListener conversationListener : conversationListeners) {
                conversationListener.conversationChange();
            }
        }
    }

    public void startConversation(User recipient, boolean newMessage) {
        StartConversationMessage message = new StartConversationMessage(this.user, recipient, newMessage);
        writeMessageToServerNoResponse(message);
    }

    public void sendMessage(String messageText, User recipient, int conversationID) {
        MessagingMessage message = new MessagingMessage(this.user, messageText, recipient, conversationID);
        writeMessageToServerNoResponse(message);
    }

    public void updateStatus(String status) {
        System.out.println("Setting status for " + this.user.getUsername());
        UpdateStatusMessage message = new UpdateStatusMessage(status, this.user);
        writeMessageToServerNoResponse(message);
    }

    public void addMessageToConversation(MessagingMessage message) {
        if (message.getSender().getUsername().equals(this.user.getUsername())) {
            this.conversationsString.put(message.getRecipient().getUsername(), message.getConversation());
            for(ConversationListener conversationListener : conversationListeners) {
                conversationListener.conversationChange();
            }
        } else {
            conversationsString.put(message.getSender().getUsername(), message.getConversation());
            lastReceivedUser = message.getSender().getUsername();
            String sender = LoginController.client.getLastReceivedUser();
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            Date now = new Date();
            String strTime = sdfTime.format(now);
            String notification = strTime + " message received from " + sender;
            userNotifications.add(0, notification);
            for(NewMessageListener messageListener : messageListeners) {
                messageListener.messageChange();
            }
        }
    }

    public void updateStatusFromServer(UpdateStatusMessage message) {
        if (message.getResponseSuccess()) {
            if(message.getUser().getUsername().equals(this.user.getUsername())) {
                this.user.setStatus(message.getStatus());
            } else {
                for (User u : onlineUsers) {
                    if (u.getUsername().equals(message.getUser().getUsername())) {
                        u.setStatus(message.getStatus());
                        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
                        Date now = new Date();
                        String strTime = sdfTime.format(now);
                        String notification = strTime + " " + u.getUsername() + " updated their status!";
                        userNotifications.add(0,notification);
                    }
                }
            }
        }
        for(StatusListener statusListener : statusListeners) {
            statusListener.updateStatus();
        }
    }


    public void close () {
        try {
            if (listener != null) {
                if (listener.isAlive()) {
                    listener.interrupt();
                }
            }
            toServer.close();
            fromServer.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("client not closed correctly");
            e.printStackTrace();
        }

    }

    public void addListener(ConversationListener conversationListener) {
        conversationListeners.add(conversationListener);
    }

    public void addListener(UserListener userListener) {
        userListeners.add(userListener);
    }

    public void addListener(NewMessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    public void addListener(AvailabilityListener availabilityListener) {
        availabilityListeners.add(availabilityListener);
    }

    public void addListener(StatusListener statusListener) {
        statusListeners.add(statusListener);
    }

}
