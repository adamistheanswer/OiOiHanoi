package Client;

import ClientServerMessages.User;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Adam Robinson & Tom Creaven
 */

public class Group implements Serializable {

  int[] picture;
  int groupNo;
  String[] name;
  String currentUser;

  ArrayList<User> group = new ArrayList<>();

  public Group(String[] Name, int Picture[], int groupNo) {
    this.name = Name;
    this.picture = Picture;
    this.groupNo = groupNo;
  }

  public String[] getName() {
    return name;
  }

  public int getGroupNo() {
    return groupNo;
  }

  public void setGroupNo(int groupNo) {
    this.groupNo = groupNo;
  }

  public void setName(String[] name) {
    this.name = name;
  }

  public int[] getPicture() {
    return picture;
  }

  public void setPicture(int[] picture) {
    this.picture = picture;
  }

  public String getCurrentUser() {
    return currentUser;
  }

  public void setCurrentUser(String currentUser) {
    this.currentUser = currentUser;
  }

}