package Client;

import ClientServerMessages.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lxf736
 * @version 2018-03-15
 */

public class ClientListener extends Thread {

  private Client client;
  private boolean interrupted;
  private Socket clientSocket;
  private ObjectOutputStream toServer;
  private ObjectInputStream fromServer;
  private final List<AvailabilityListener> availabilityListeners = new ArrayList<>();

  public ClientListener(Client client, Socket clientSocket, ObjectOutputStream toServer,
      ObjectInputStream fromServer) {
    this.client = client;
    this.clientSocket = clientSocket;
    this.toServer = toServer;
    this.fromServer = fromServer;
  }

  public void run() {
    while (true) {
      try {
        if (!interrupted) {
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
        interrupted = true;
        System.out.println("Server could not be reached");
        for(AvailabilityListener availabilityListener : availabilityListeners) {
          availabilityListener.serverAvailable();
        }
      }
    }
  }

  /*Some of these methods to go in the ClientServerMessages package*/
  public Message interpretMessage() throws IOException {
    Message message = null;
    try {
      message = (Message) fromServer.readObject();
    } catch (ClassNotFoundException e) {
      System.out.println(
          "The returned UserMessage type was not recognised! Please ensure it has been imported");
    }
    if (message != null) {
      return message;
    } else {
      return null;
    }
  }

  public void processMessage(Message message) throws IllegalArgumentException, IOException {
    if (message instanceof UpdateOnlineUsersMessage) {
      client.updateOnlineUsers((UpdateOnlineUsersMessage) message);
    } else if (message instanceof StartConversationMessage) {
      client.updateConversationsList((StartConversationMessage) message);
    } else if (message instanceof MessagingMessage) {
      client.addMessageToConversation((MessagingMessage) message);
    } else if (message instanceof UpdateStatusMessage) {
      client.updateStatusFromServer((UpdateStatusMessage) message);
    } else {
      throw new IllegalArgumentException("Command identifier not recognised");
    }
  }

  public void addAvailabilityListener(AvailabilityListener availabilityListener) {
    availabilityListeners.add(availabilityListener);
  }

}
