package Client;

import ClientServerMessages.SignUpMessage;
import java.util.Objects;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Adam Robinson & Tom Creaven
 */


public class SignUpController implements Initializable {

  @FXML private Button HomeButton;
  @FXML private Button SignUpAcceptButton;
  @FXML private ImageView PPDefault;
  @FXML private ImageView PP1;
  @FXML private ImageView PP2;
  @FXML private ImageView PP3;
  @FXML private ImageView PP4;
  @FXML private ImageView PP5;
  @FXML private ChoiceBox<? extends String> imagePicker;
  @FXML private Label selectedPicture;
  @FXML private Label SignUpLabel;
  @FXML private TextField emailField;
  @FXML private TextField usernameField;
  @FXML private PasswordField passwordField;
  @FXML private PasswordField confirmPasswordField;
  private int avatarNumber = 0;

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

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    imagePicker.getSelectionModel().selectFirst();
    selectedPicture.textProperty().bind(imagePicker.getSelectionModel().selectedItemProperty());
    selectedPicture.setVisible(false);

    HomeButton.setOnAction((ActionEvent event) -> {
      Parent root = null;
      try {
        root = FXMLLoader.load(getClass().getResource("../Views/login.fxml"));
      } catch (IOException e) {
        e.printStackTrace();
      }
      Scene scene = new Scene(Objects.requireNonNull(root), 350, 600);
      Launcher.primaryStage.setScene(scene);
      Launcher.primaryStage.setResizable(false);
      Launcher.primaryStage.centerOnScreen();
      Launcher.primaryStage.show();
    });

    SignUpAcceptButton.setOnAction((ActionEvent event) -> {

      if (!validateEmail(emailField.getText())) {
        SignUpLabel.setText("Please Enter Valid Email");
        SignUpLabel.setTextFill(Color.rgb(255, 0, 0));
      } else if (!validateAlphanumeric(usernameField.getText())) {
        SignUpLabel.setText("Username Containing A-Z 0-9 Only");
        SignUpLabel.setTextFill(Color.rgb(255, 0, 0));
      } else if (usernameField.getText().equals("")) {
        SignUpLabel.setText("Please Enter a Username");
        SignUpLabel.setTextFill(Color.rgb(255, 0, 0));
      } else if (usernameField.getText().length() < 5 || usernameField.getText().length() > 20) {
        SignUpLabel.setText("Username Length Between 5 - 20 Characters");
        SignUpLabel.setTextFill(Color.rgb(255, 0, 0));
      } else if (passwordField.getText().equals("")) {
        SignUpLabel.setText("Please Enter Password");
        SignUpLabel.setTextFill(Color.rgb(255, 0, 0));
      } else if (!validateAlphanumeric(passwordField.getText())) {
        SignUpLabel.setText("Password Containing A-Z 0-9 Only");
        SignUpLabel.setTextFill(Color.rgb(255, 0, 0));
      } else if (passwordField.getText().length() < 8 || passwordField.getText().length() > 20) {
        SignUpLabel.setText("Password Length Between 8 - 20 Characters");
        SignUpLabel.setTextFill(Color.rgb(255, 0, 0));
      } else if (confirmPasswordField.getText().equals("")) {
        SignUpLabel.setText("Please Confirm Password");
      } else if (!passwordField.getText().equals(confirmPasswordField.getText())) {
        SignUpLabel.setText("Passwords Don't Match!");
        SignUpLabel.setTextFill(Color.rgb(255, 0, 0));
      } else {
        SignUpLabel.setTextFill(Color.rgb(0, 153, 51));
        SignUpMessage response = LoginController.client
            .signUp(emailField.getText().toLowerCase(), usernameField.getText().toLowerCase(),
                BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt()), avatarNumber);
        if (response.getResponseSuccess()) {
          LoginController.client.close();
          SignUpLabel.setText(response.getResponseMessage());
          SignUpAcceptButton.setDisable(true);
        } else {
          SignUpLabel.setTextFill(Color.rgb(255, 0, 0));
          SignUpLabel.setText(response.getResponseMessage());
        }
      }
    });

    imagePicker.getSelectionModel().selectedItemProperty()
        .addListener((ChangeListener<String>) (selected, oldPicture, newPicture) -> {
          if (oldPicture != null) {
            switch (oldPicture) {
              case "Default Avatar":
                PPDefault.setVisible(false);
                break;
              case "Avatar 1":
                PP1.setVisible(false);
                break;
              case "Avatar 2":
                PP2.setVisible(false);
                break;
              case "Avatar 3":
                PP3.setVisible(false);
                break;
              case "Avatar 4":
                PP4.setVisible(false);
                break;
              case "Avatar 5":
                PP5.setVisible(false);
                break;
            }
          }

          if (newPicture != null) {
            switch (newPicture) {
              case "Default Avatar":
                PPDefault.setVisible(true);
                avatarNumber = 0;
                System.out.println("Avatar Number: " + avatarNumber);
                break;
              case "Avatar 1":
                PP1.setVisible(true);
                avatarNumber = 1;
                System.out.println("Avatar Number: " + avatarNumber);
                break;
              case "Avatar 2":
                PP2.setVisible(true);
                avatarNumber = 2;
                System.out.println("Avatar Number: " + avatarNumber);
                break;
              case "Avatar 3":
                PP3.setVisible(true);
                avatarNumber = 3;
                System.out.println("Avatar Number: " + avatarNumber);
                break;
              case "Avatar 4":
                PP4.setVisible(true);
                avatarNumber = 4;
                System.out.println("Avatar Number: " + avatarNumber);
                break;
              case "Avatar 5":
                PP5.setVisible(true);
                avatarNumber = 5;
                System.out.println("Avatar Number: " + avatarNumber);
                break;
            }
          }
        });
  }
}
