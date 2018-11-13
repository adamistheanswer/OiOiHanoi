package Client;

import static Client.Launcher.primaryStage;

import ClientServerMessages.SignInMessage;
import java.util.Objects;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.stage.Stage;

/**
 * @author Adam Robinson & Tom Creaven
 */

public class LoginController implements Initializable {

  public static Client client;
  @FXML private TextField user;
  @FXML private PasswordField password;
  @FXML private TextField serverField;
  @FXML private TextField portField;
  @FXML private Button loginButton;
  @FXML private Button SignUpButton;
  @FXML private Hyperlink ResetPassword;
  @FXML private Label SignUpLabel;



  @Override
  public void initialize(URL location, ResourceBundle resources) {

    Alert serverDown = null;

    primaryStage.setOnCloseRequest(evt -> {
      evt.consume();
      Platform.exit();
    });

    loginButton.setOnAction((ActionEvent event) -> {
      try {
        client = new Client(serverField.getText(), Integer.parseInt(portField.getText()));
      } catch(IOException e) {
        SignUpLabel.setText("Server Unavailable");
      }
      if (client != null) {
        SignInMessage response = client.signIn(user.getText().toLowerCase(), password.getText());
        if(response != null) {
          if (response.getResponseSuccess()) {
            {
              client.setUser(response.getUser());
              client.setOnlineUsers(response.getOnlineUsers());
              Parent root = null;
              try {
                root = FXMLLoader.load(getClass().getResource("../Views/mainchat.fxml"));
              } catch (IOException e) {
                e.printStackTrace();
              }

              Scene scene = new Scene(Objects.requireNonNull(root), 1280, 720);

              Launcher.primaryStage.setScene(scene);
              Launcher.primaryStage.setResizable(false);
              Launcher.primaryStage.centerOnScreen();
              Launcher.primaryStage.show();
            }
          } else {
            SignUpLabel.setText(response.getResponseMessage());
          }
        } else {
          SignUpLabel.setText("Server Unavailable");
        }
      }
    });


    SignUpButton.setOnAction((ActionEvent event) -> {
      if(!serverField.getText().isEmpty() && !portField.getText().isEmpty()) {
        try {
          client = new Client(serverField.getText(), Integer.parseInt(portField.getText()));
        } catch(IOException e) {
          SignUpLabel.setText("Server Unavailable");
        }
        if (client != null) {
          Parent root = null;
          try {
            root = FXMLLoader.load(getClass().getResource("../Views/signup.fxml"));
          } catch (IOException e) {
            e.printStackTrace();
          }
          Scene scene = new Scene(Objects.requireNonNull(root), 350, 600);

          Launcher.primaryStage.setScene(scene);
          Launcher.primaryStage.setResizable(false);
          Launcher.primaryStage.centerOnScreen();
          Launcher.primaryStage.show();
        }
      } else {
          SignUpLabel.setText("Enter Server IP & Port");
      }
    });

    ResetPassword.setOnAction((ActionEvent event) -> {
      try {
        client = new Client(serverField.getText(), Integer.parseInt(portField.getText()));
      } catch(IOException e) {
        SignUpLabel.setText("Server Unavailable");
      }
      if (client != null) {
        Parent root = null;
        try {
          root = FXMLLoader.load(getClass().getResource("../Views/forgotPassword.fxml"));
        } catch (IOException e) {
          e.printStackTrace();
        }
        Scene scene = new Scene(Objects.requireNonNull(root), 350, 400);
        Launcher.primaryStage.setScene(scene);
        Launcher.primaryStage.setResizable(false);
        Launcher.primaryStage.centerOnScreen();
        Launcher.primaryStage.show();
      }
    });

  }
}