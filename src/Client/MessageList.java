package Client;


import ClientServerMessages.User;
import ClientServerMessages.UserMessage;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;

import static Client.ChatController.avatarThumb;

/**
 * @author Adam Robinson & Tom Creaven
 */

public class MessageList implements Callback<ListView<UserMessage>, ListCell<UserMessage>> {

  @Override
  public ListCell<UserMessage> call(ListView<UserMessage> p) {

    ListCell<UserMessage> cell = new ListCell<UserMessage>() {

      @Override
      protected void updateItem(UserMessage message, boolean bln) {
        super.updateItem(message, bln);
        setGraphic(null);
        setText(null);
        if (message != null) {
          HBox container = new HBox();

          StringBuilder sb = new StringBuilder(message.getContent());

          int i = 0;
          while (i + 120 < sb.length() && (i = sb.lastIndexOf(" ", i + 120)) != -1) {
            sb.replace(i, i + 1, "\n");
          }

          Text sender = new Text("    " + message.getSendingUser().getUsername());
          Text content = new Text(sb.toString());
          Text time = new Text("    " + message.getStringTime() + "    ");

          Text sender2 = new Text(message.getSendingUser().getUsername() + "    " );


//          ImageView avatar = new ImageView();
//          Image image = new Image(
//              getClass().getClassLoader().getResource(avatarThumb(message.getAvatar())).toString(), 50,
//              50, false, false);
//          avatar.setImage(image);

         // container.getChildren().addAll(sender, time, content);

          if (message.getSendingUser().getUsername().equals(LoginController.client.getUser().getUsername())) {
            sender2.setFont(Font.font(null, FontWeight.BOLD, 14));
            container.getChildren().addAll(content, time, sender2);
            container.setAlignment(Pos.CENTER_RIGHT);
            container.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: #5CB7EC;");
          }
          else {
            container.setAlignment(Pos.CENTER_LEFT);
            sender.setFont(Font.font(null, FontWeight.BOLD, 14));
            container.getChildren().addAll(sender, time, content);
            container.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: #67CA6D;");
          }

          setGraphic(container);
        }
      }
    };
    return cell;
  }


}


