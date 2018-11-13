package Client;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * @author Adam Robinson & Tom Creaven
 */

public class Launcher extends Application {

  static Stage primaryStage;

  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Main Application launcher
   * Creates JFX stage (350px x 600px) and loads login FXML file as first scene
   *
   * @param stage Primary stage initialised on application start, this stage used throughout with scene changing
   * @throws IOException Handled if FXMLLoader unable to locate login.FXML
   */

  @Override
  public void start(Stage stage) throws IOException {
    Scene scene = new Scene(new VBox(), 350, 600);
    primaryStage = stage;
    FXMLLoader loader = new FXMLLoader(getClass().getResource("../Views/login.fxml"));
    scene.setRoot(loader.load());
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }
}

