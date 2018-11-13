package Client;

import static Client.Launcher.primaryStage;

import ClientServerMessages.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.net.URL;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Adam Robinson & Tom Creaven
 */

public class ChatController implements Initializable {

  @FXML
  private Button SendMsg;

  @FXML
  private TextArea messageBox;
  @FXML
  private Label username;
  @FXML
  private ImageView avatar;
  @FXML
  private TextField statusField;
  @FXML
  private Button statusButton;
  @FXML
  private ListView chatView;
  @FXML
  private ListView singleUserList;
  @FXML
  private ListView notificationsList;
  @FXML
  private Label numberOfOnlineUsers;
  private boolean disconnection;

  /**
   * Returns the file location and name as a string based on the avatar number stored in your user
   * class instance Full size for display at top of chat window
   *
   * @param avatarNumber avatar identifier stored as int and assigned at signup
   * @return String location and file name of .PNG image
   */

  public static String avatarSetter(int avatarNumber) {
    if (avatarNumber == 0) {
      return "img/Avatar.png";
    }
    if (avatarNumber == 1) {
      return "img/PP1.png";
    }
    if (avatarNumber == 2) {
      return "img/PP2.png";
    }
    if (avatarNumber == 3) {
      return "img/PP3.png";
    }
    if (avatarNumber == 4) {
      return "img/PP4.png";
    } else {
      return "img/PP5.png";
    }
  }

  /**
   * Returns the file location and name as a string based on the avatar number stored in your user
   * class instance Thumb size for display in group chat user online list
   *
   * @param avatarNumber avatar identifier stored as int and assigned at signup
   * @return String location and file name of .PNG image
   */

  public static String avatarThumb(int avatarNumber) {
    if (avatarNumber == 0) {
      return "img/avatar50.png";
    }
    if (avatarNumber == 1) {
      return "img/PP150.png";
    }
    if (avatarNumber == 2) {
      return "img/PP250.png";
    }
    if (avatarNumber == 3) {
      return "img/PP350.png";
    }
    if (avatarNumber == 4) {
      return "img/PP450.png";
    } else {
      return "img/PP550.png";
    }
  }

  public void serverDown() {
    Platform.runLater(() -> {
      Alert serverDown = new Alert(AlertType.WARNING, "Server Disconnected - Log Out", ButtonType.FINISH);
      if (serverDown.showAndWait().orElse(ButtonType.NO) == ButtonType.FINISH) {
        serverDown = null;
        Parent root = null;
        try {
          root = FXMLLoader.load(getClass().getResource("../Views/login.fxml"));
        } catch (IOException e) {
          e.printStackTrace();
        }

        Scene scene = new Scene(Objects.requireNonNull(root));
        LoginController.client.close();
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
      }
    });
  }

  private void shutdown(Stage primaryStage) {
    Alert alert = new Alert(Alert.AlertType.NONE, "Are you sure you want to sign out?",
        ButtonType.YES, ButtonType.NO);
    if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
        LoginController.client.close();
        System.exit(0);
    }
  }

  /**
   * Updates user list based on message returned from server when change occurred
   */

  public void setUserList() {
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        ObservableList<User> users = FXCollections.observableList(LoginController.client.getOnlineUsers());
        users.remove(LoginController.client.getUser());
        if(!LoginController.client.getOnlineUsers().contains(LoginController.client.getCurrentlySelectedUser())) {
          chatView.getItems().clear();
          LoginController.client.setCurrentlySelectedUser(null);
        }
        singleUserList.setItems(users);
        singleUserList.setCellFactory(new SingleList());
        numberOfOnlineUsers.setText(String.valueOf(LoginController.client.getOnlineUsers().size()));
      }
    });
  }

  public void notification() {
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        if(LoginController.client.getCurrentlySelectedUser()!= null) {
          if (LoginController.client.getCurrentlySelectedUser().equals(LoginController.client.getLastReceivedMessage())) {
            setChatView();
            ObservableList<User> users = FXCollections.observableList(LoginController.client.getOnlineUsers());
            singleUserList.setItems(users);
            singleUserList.setCellFactory(new SingleList());
            numberOfOnlineUsers.setText(String.valueOf(LoginController.client.getOnlineUsers().size()));
          } else {
            ArrayList<String> notifications = LoginController.client.getNotifications();
            ObservableList<String> listNotifications = FXCollections.observableList(notifications);
            notificationsList.setItems(listNotifications);
            ObservableList<User> users = FXCollections.observableList(LoginController.client.getOnlineUsers());
            singleUserList.setItems(users);
            singleUserList.setCellFactory(new SingleList());
            numberOfOnlineUsers.setText(String.valueOf(LoginController.client.getOnlineUsers().size()));
          }
        } else {
          ArrayList<String> notifications = LoginController.client.getNotifications();
          ObservableList<String> listNotifications = FXCollections.observableList(notifications);
          notificationsList.setItems(listNotifications);
          ObservableList<User> users = FXCollections.observableList(LoginController.client.getOnlineUsers());
          singleUserList.setItems(users);
          singleUserList.setCellFactory(new SingleList());
          numberOfOnlineUsers.setText(String.valueOf(LoginController.client.getOnlineUsers().size()));

        }
      }
    });
  }

  public void setChatView() {
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
          Conversation conversation = LoginController.client.getConversation(LoginController.client.getCurrentlySelectedUser());
        if(!(conversation == null) && !(conversation.getConvoUserMessages() == null )) {
          ObservableList<UserMessage> chat = FXCollections.observableList(conversation.getConvoUserMessages());
          chatView.setItems(chat);
          chatView.setCellFactory(new MessageList());
          chatView.scrollTo(chat.size()-1);
        }
      }
    });
  }

  public User findUser(String selection) {
    for (User el : LoginController.client.getOnlineUsers()) {
      if (el.getUsername().equals(selection)) {
        return el;
      }
    }
    return null;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

     username.setText(LoginController.client.getUser().getUsername());

    // Closes client thread on window close and logs user out of system
    primaryStage.setOnCloseRequest(evt -> {
      evt.consume();
      shutdown(primaryStage);
    });

    LoginController.client.addListener(new ConversationListener() {
      @Override
      public void conversationChange() {
        setChatView();
      }
    });

    LoginController.client.addListener(new NewMessageListener() {
      @Override
      public void messageChange() {
        notification();
      }
    });

    LoginController.client.addListener(new UserListener() {
      @Override
      public void userChange() {
        setUserList();
      }
    });

    LoginController.client.addListener(new StatusListener() {
      @Override
      public void updateStatus() {
        System.out.println("REACHING CODE TO UPDATE USER STATUS!!");
        notification();
      }
    });


    setUserList();
    LoginController.client.startListener();
    avatar.setImage(new Image(avatarSetter(LoginController.client.getUser().getAvatar())));

    LoginController.client.getListener().addAvailabilityListener(new AvailabilityListener() {
      @Override
      public void serverAvailable() {
        System.out.println("chatController has detected that server is down!");
        if(!disconnection)
          disconnection = true;
        serverDown();
        }
    });


    /**
     *  Handles selection of SingleList cell from online user list
     */

    singleUserList.setOnMouseClicked(new ListViewHandler() {
      @Override
      public void handle(javafx.scene.input.MouseEvent event) {
        if (!singleUserList.getItems().isEmpty()) {
          SendMsg.setDisable(false);
          String selection = singleUserList.getSelectionModel().getSelectedItem().toString();
          User recipient = findUser(selection);
          LoginController.client.setCurrentlySelectedUser(selection);
          if (LoginController.client.getConversation(selection) == null) {
              LoginController.client.startConversation(recipient, true);
          } else
              setChatView();
        }
      }
    });



    SendMsg.setOnAction((ActionEvent event) -> {
      if(!messageBox.getText().isEmpty()) {
        //String selection = singleUserList.getSelectionModel().getSelectedItem().toString();
        String selection = LoginController.client.getCurrentlySelectedUser();
        User recipient = findUser(selection);
        Conversation conversation = LoginController.client.getConversation(selection);
        if (!(recipient == null)) {
          LoginController.client.sendMessage(messageBox.getText(), recipient, conversation.getId());
          messageBox.setText("");
        }
      }
    });

    statusButton.setOnAction((ActionEvent event) -> {
      if(!statusField.getText().isEmpty()) {
        LoginController.client.updateStatus(statusField.getText());
        statusField.setText("");
        }
      });


  }
}



