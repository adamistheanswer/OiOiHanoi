package Client;

import ClientServerMessages.ResetPasswordMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Adam Robinson & Tom Creaven
 */

public class ForgotPasswordController implements Initializable {

  @FXML private Label signUpLabel;
  @FXML private TextField email;
  @FXML private Button home;
  @FXML private Button request;
  public static String emailAddress;

  private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

  private static boolean validateEmail(String emailStr) {
    Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
    return matcher.find();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    /**
     * Home button controller changes scene on Primary stage back to Login.FXML
     * Resets stage dimensions to 350 x 600
     */

    home.setOnAction((ActionEvent event) -> {
      Parent root = null;
      try {
        root = (Parent) FXMLLoader.load(getClass().getResource("../Views/login.fxml"));
      } catch (IOException e) {
        e.printStackTrace();
      }
      Scene scene = new Scene(root, 350, 600);
      Launcher.primaryStage.setScene(scene);
      Launcher.primaryStage.setResizable(false);
      Launcher.primaryStage.centerOnScreen();
      Launcher.primaryStage.show();
    });

    request.setOnAction((ActionEvent event) -> {
      if (!validateEmail(email.getText())) {
        signUpLabel.setText("Please Enter Valid Email Address");
        signUpLabel.setTextFill(Color.rgb(255, 0, 0));
      } else {
        emailAddress = email.getText();
        ResetPasswordMessage response = LoginController.client.resetPassword(email.getText());
        if (response.getResponseSuccess() == true) {
          Parent root = null;
          try {
            root = (Parent) FXMLLoader.load(getClass().getResource("../Views/changePassword.fxml"));
          } catch (IOException e) {
            e.printStackTrace();
          }
          Scene scene = new Scene(root, 350, 600);
          Launcher.primaryStage.setScene(scene);
          Launcher.primaryStage.setResizable(false);
          Launcher.primaryStage.centerOnScreen();
          Launcher.primaryStage.show();
//          signUpLabel.setText("We've sent a password change request to" + "\n" + email.getText());
        } else {
          signUpLabel.setText(response.getResponseMessage());
        }
      } //signUpLabel.setText("Please check your email");
      signUpLabel.setTextFill(Color.rgb(56, 44, 81));
    });
  }
}