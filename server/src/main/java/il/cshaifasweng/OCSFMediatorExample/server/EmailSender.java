package il.cshaifasweng.OCSFMediatorExample.server;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailSender {

    /*
    Usage: EmailSender.sendEmail(
                    "sharbelma3@gmail.com",              // recipient
                    "Mom's Kitchen",          // subject
                    "Test from java" // body
            );
     */


    private static final String FROM_EMAIL = "mtraders191223@gmail.com";
    private static final String APP_PASSWORD = "dwam hewa nwmu hsks"; // App password

    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });
    }

    public static void sendEmail(String to, String subject, String body) {
        try {
            Message message = new MimeMessage(createSession());
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent to " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
