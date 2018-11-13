package Client;

import ClientServerMessages.User;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import static Client.ChatController.avatarThumb;

/**
 * @author Adam Robinson & Tom Creaven
 */

public class SingleList implements Callback<ListView<User>, ListCell<User>> {


  public static String username;
  public static User currentUser;

  @Override
  public ListCell<User> call(ListView<User> p) {

    ListCell<User> cell = new ListCell<User>() {

      @Override
      protected void updateItem(User user, boolean bln) {
        super.updateItem(user, bln);
        setGraphic(null);
        setText(null);
        if (user != null) {
          HBox container = new HBox();

          Text name = new Text("    " + user.getUsername());
          Text status = new Text("   OIOI!!");
          if (user.getStatus() != null) {
            status = new Text("   " + user.getStatus());
          }

          ImageView avatar = new ImageView();
          Image image = new Image(
              getClass().getClassLoader().getResource(avatarThumb(user.getAvatar())).toString(), 50,
              50, false, false);
          avatar.setImage(image);
          username = user.getUsername();
          currentUser = user;

          container.getChildren().addAll(avatar, name, status);
          container.setAlignment(Pos.CENTER_LEFT);

          setGraphic(container);
        }
      }
    };
    return cell;
  }

  @Override
  public String toString() {
    return username;
  }

  public static User getUser() {
    return currentUser;
  }

}


