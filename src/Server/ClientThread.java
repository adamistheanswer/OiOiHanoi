package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ClientServerMessages.*;
import ClientServerMessages.Message;

/**
 * @author lxf736
 * @version 2018-03-04
 */

public class ClientThread extends Thread {

    private Server server;
    private Socket socketForClient;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;
    private User user;
    private boolean stopped;

    public ClientThread(Server server, Socket socketForClient)  {

        this.server = server;
        this.socketForClient = socketForClient;

        try {
            fromClient = new ObjectInputStream(socketForClient.getInputStream());
            toClient = new ObjectOutputStream(socketForClient.getOutputStream());
        }  catch (IOException e) {
            System.out.println("client failed to establish connection with server at socket " + socketForClient);
            e.printStackTrace();
        }

    }

    public void run(){
        while(true) {
            try {
                if (!stopped) {
                    Message currentMessage = interpretMessage();
                    if (currentMessage != null) {
                        processMessage(currentMessage);
                    }
                } else {
                    break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Arguments passed by client not recognised");
            } catch (IOException e) {
                System.out.println("Client could not be reached, terminating thread");
                stopThread();
            }
        }
    }

    public Message interpretMessage() throws IOException {
        Message message = null;
        try {
            message = (Message)fromClient.readObject();
        } catch (ClassNotFoundException e) {
            System.out.println("The returned UserMessage type was not recognised! Please ensure it has been imported");
        }
        if (message != null) {
            return message;
        } else {
            return null;
        }
    }

    public void processMessage(Message message) throws IllegalArgumentException, IOException {
        if (message instanceof SignUpMessage) {
            System.out.println("Server received SignUpMessage from client");
            server.signUp((SignUpMessage)message, this);
        } else if (message instanceof SignInMessage) {
            System.out.println("Server received SignInMessage from client");
            server.signIn((SignInMessage) message, this);
        } else if (message instanceof StartConversationMessage) {
            System.out.println("Server received StartConversationMessage from client");
            server.newConversation((StartConversationMessage) message, this);
        } else if(message instanceof MessagingMessage) {
            System.out.println("Server received MessagingMessage from client");
            server.newMessage((MessagingMessage) message, this);
        } else if(message instanceof ResetPasswordMessage) {
            System.out.println("Server received ResetPasswordMessage from client");
            server.resetPassword((ResetPasswordMessage) message, this);
        } else if(message instanceof ChangePasswordMessage) {
            System.out.println("Server receied ChangePasswordMessage from client");
            server.changePassword((ChangePasswordMessage) message, this);
        } else if(message instanceof UpdateStatusMessage) {
            System.out.println("Received update status message from server");
            server.updateUserStatus((UpdateStatusMessage) message, this);
        } else {
            throw new IllegalArgumentException("Command identifier not recognised");
        }
    }

    public ObjectOutputStream getToClient() {
        return toClient;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public void updateOnlineUsers(User newUser, boolean login) throws IOException {
        toClient.writeObject(new UpdateOnlineUsersMessage(newUser, login));
    }

    public void updateUserStatus(UpdateStatusMessage message) throws IOException {
        toClient.writeObject(message);
    }


    public void stopThread() {
        this.stopped = true;
        try {
            socketForClient.close();
            System.out.println(socketForClient + " connection closed");
        } catch (IOException e) {
            System.out.println( socketForClient + " socket not Closed!");
        }
        this.interrupt();
        if (this.user != null) {
            this.server.signOutUser(this.user);
        }
        System.out.println(this.getName() + " terminated");
    }

    public String constructSendingThread(Thread thread) {
        return "Sending via " + thread.getName() + ": ";
    }

}
