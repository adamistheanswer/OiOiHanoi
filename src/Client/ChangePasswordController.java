package Client;

import ClientServerMessages.ChangePasswordMessage;
import ClientServerMessages.Conversation;
import ClientServerMessages.ResetPasswordMessage;
import ClientServerMessages.User;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

/**
 * @author Adam Robinson & Tom Creaven
 */

public class ChangePasswordController implements Initializable {

  @FXML private Label changePasswordLabel;
  @FXML private TextField resetCode;
  @FXML private PasswordField password1;
  @FXML private PasswordField password2;
  @FXML private Button home;
  @FXML private Button reset;


  public static final Pattern VALID_ALPHANUMERIC_REGEX =
      Pattern.compile("^[a-zA-Z0-9_+-]*$", Pattern.CASE_INSENSITIVE);

  public static boolean validateAlphanumeric(String alpha) {
    Matcher matcher = VALID_ALPHANUMERIC_REGEX.matcher(alpha);
    return matcher.find();
  }


  @Override
  public void initialize(URL location, ResourceBundle resources) {

   // reset.setDisable(true);

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

    reset.setOnAction((ActionEvent event) -> {
      if (resetCode.getText().isEmpty()) {
        changePasswordLabel.setText("Please enter reset code");
        changePasswordLabel.setTextFill(Color.rgb(255, 0, 0));
      } else if (!validateAlphanumeric(password1.getText()) || !validateAlphanumeric(password2.getText())) {
        changePasswordLabel.setText("Password Containing A-Z 0-9 Only");
        changePasswordLabel.setTextFill(Color.rgb(255, 0, 0));
      } else if (password1.getText().length() < 8 || password1.getText().length() > 20) {
        changePasswordLabel.setText("Password Length Between 8 - 20 Characters");
        changePasswordLabel.setTextFill(Color.rgb(255, 0, 0));
      } else if (password2.getText().equals("")) {
        changePasswordLabel.setText("Please Confirm Password");
      } else if (!password1.getText().equals(password2.getText())) {
        changePasswordLabel.setText("Passwords Don't Match!");
        changePasswordLabel.setTextFill(Color.rgb(255, 0, 0));
      } else {
        ChangePasswordMessage response = LoginController.client.changePassword(ForgotPasswordController.emailAddress, resetCode.getText(), password1.getText());
        if (response.getResponseSuccess()) {
          changePasswordLabel.setText(response.getResponseMessage());
        } else {
          changePasswordLabel.setText(response.getResponseMessage());
        }
      }
  });


    }
  }
