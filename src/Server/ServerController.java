package Server;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * @author Adam Robinson
 */

public class ServerController implements Initializable {

    @FXML private TextField hostField;
    @FXML private TextField portField;
    @FXML private Button connectButton;
    @FXML private Button disconnectButton;
    @FXML private Label conStatus;
    private Server server;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        disconnectButton.setDisable(true);

        connectButton.setOnAction((ActionEvent event) -> {
            if(!hostField.getText().isEmpty() && !portField.getText().isEmpty()) {
                try {
                    server = new Server(hostField.getText(), Integer.parseInt(portField.getText()), 100);
                    server.start();
                    disconnectButton.setDisable(false);
                    if (server.getServerRunning()) {
                        conStatus.setText("Server Running");
                        connectButton.setDisable(true);
                    } else {
                        conStatus.setText("Start Server");
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("Could not connect to database");
                } catch (IOException e) {
                    System.out.println("Server could not be started with provided args! Please ensure args are correct.");
                    conStatus.setText("Unable to start server at above host / port");
                } catch (SQLException e) {
                    System.out.println("Unable to establish Database connection");
                    conStatus.setText("Unable to establish Database connection");
                    server.stopServer();
                } catch (NumberFormatException e) {
                    System.out.println("Port number not recognised as a valid value");
                    conStatus.setText("Port number not recognised as a valid value");
                }
            } else {
                conStatus.setText("Please enter a host name");
                System.out.println("Please enter a host name");
            }
        });

        disconnectButton.setOnAction((ActionEvent event) -> {
            server.stopServer();
            conStatus.setText("Server Stopped");
            connectButton.setDisable(false);
        });

    }
}
