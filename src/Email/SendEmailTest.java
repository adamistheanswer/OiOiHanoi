
package Email;
import java.io.IOException;
import java.util.ArrayList;

import javax.mail.MessagingException;

public class SendEmailTest {
	
	public static void main (String args[]) {
		ArrayList<String> testAddresses = new ArrayList<String>();
		testAddresses.add("flee17@hotmail.co.uk");
		try {
			Email.sendEmail(testAddresses, "OiOiHannoi - delivering spam since 2018", "You've been spammed by OiOiHanoi...");
		} catch (IOException e) {
			System.out.println("a properties file was not found by email class");
		} catch (MessagingException e) {
			e.printStackTrace();
				System.out.println("messaging exception thrown");
		}
	}


}

