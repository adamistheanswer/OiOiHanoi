package Email;
/**
 * A static class containing methods which provide email functionality.
 * Please note that there are two .properties files used by this class - email.properties and
 * transport.properties. These contain values used within the class and have been stored as .properties
 * files to allow the ability to make config changes without needing to recompile this class.
 * ALSO NOTE: original config utilised a gmail account, as such sent emails are saved by gmail as standard,
 * ideally we would not save emails as this leads to issues around user privacy / data protection.
 * @author lxf736
 * @version 2018-02-27
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email {
	
	/**
	 * A method to Send Email. The method requires the following params:
	 * @param toAddress is an ArrayList<String which represents the email addresses of the email recipients
	 * @param subject is the subject line of the email - must not contain line breaks
	 * @param message is the body of the email 
	 * @throws IOException when properties files cannot be found (i.e. "email.properties" & "transport.properties")
	 * @throws MessagingException if any address contained within toAddress is invalid, if param subject contains 
	 * any line breaks, if  subject contains any non US-ASCII characters that fail to encode or if transport connection
	 * fails.
	 */
	public static void sendEmail (ArrayList<String> toAddress, String subject, String message) throws IOException, MessagingException {

		//Read Session properties
		FileInputStream emailPropsInput = new FileInputStream(new File("email.properties"));
		Properties emailProps = new Properties();
		emailProps.load(emailPropsInput);
		emailPropsInput.close();
 
		// Create Session and UserMessage
		Session emailSession = Session.getDefaultInstance(emailProps, null);
		MimeMessage email = new MimeMessage(emailSession);
		for (int i = 0; i < toAddress.size(); i++) {
			email.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress.get(i)));
		}
		email.setSubject(subject);
		email.setContent(message, "text/html");
		
		//Read Transport properties
		FileInputStream transportInput = new FileInputStream(new File("transport.properties"));
		Properties transportProps = new Properties();
		transportProps.load(transportInput);
		transportInput.close();
		Transport transport = emailSession.getTransport(transportProps.getProperty("protocol"));

		//Establish connection with email server
		transport.connect(transportProps.getProperty("server"), transportProps.getProperty("fromAddress"), 
				transportProps.getProperty("fromAddressPW"));	
		
		//Send email
		transport.sendMessage(email, email.getAllRecipients());
		transport.close();

	}
	

}
