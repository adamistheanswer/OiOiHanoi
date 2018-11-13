package Client;

import ClientServerMessages.SignInMessage;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author lxf736
 * @version 2018-03-01
 */

public class ClientMainTest {


    public static void main(String[] args) throws IOException {
        Client client = new Client (args[0], Integer.parseInt(args[1]));
       // client.start();
        Scanner inp = new Scanner(System.in);
        printCommands();
        String command = inp.nextLine();
        while (true) {
            try {
                interpretInput(Integer.parseInt(command), client);
                printCommands();
                command = inp.next();
            } catch (Exception e) {
                System.out.println("Please ensure to enter a valid command");
                printCommands();
                command = inp.next();
            }
        }
    }

    public static void printCommands() {
        System.out.println("Enter following commands to test client functionality:");
        System.out.println("Enter '1' to test signing in");
        System.out.println("Enter '2' to test signing up");
        System.out.println("Enter '0' to close client");
    }

    public static void interpretInput(int command, Client client) {

        Scanner inp = new Scanner(System.in);

        if (command == 0) {
            System.exit(0);

        } else if (command == 1){
            System.out.println("please enter an existing user email address");
            String email = inp.nextLine();
            System.out.println("please enter the users password");
            String pw = inp.nextLine();

            SignInMessage response = client.signIn(email, pw);

            if (response.getResponseSuccess()) {
                client.setUser(response.getUser());
                client.setOnlineUsers(response.getOnlineUsers());
                client.startListener();
                System.out.println("test sending a message");

            } else {
                System.out.println(response.getResponseMessage());
                }

        } else if (command == 2){
            System.out.println("please enter an email address");
            String email = inp.nextLine();
            System.out.println("please enter a username");
            String username = inp.nextLine();
            System.out.println("please enter a password");
            String password = inp.nextLine();
            System.out.println("please enter an avatar");
            String avatar = inp.nextLine();
            client.signUp(email, username, password, Integer.parseInt(avatar));
        }

    }

}
