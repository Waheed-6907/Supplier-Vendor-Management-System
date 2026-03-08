package ui;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailSender {

    private static final String FROM_EMAIL = "suppliervendormanagement@gmail.com";
    private static final String APP_PASSWORD = "vtubnyuteacctsgr";

    private static void sendEmail(String toEmail, String subject, String body) {

        try {

            Properties props = new Properties();

            props.put("mail.smtp.auth","true");
            props.put("mail.smtp.starttls.enable","true");
            props.put("mail.smtp.host","smtp.gmail.com");
            props.put("mail.smtp.port","587");

            Session session = Session.getInstance(props,
                    new Authenticator() {

                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
                        }

                    });

            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(FROM_EMAIL));

            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );

            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            System.out.println("Email sent successfully to " + toEmail);

        }
        catch(Exception e){

            System.out.println("Email sending failed");
            e.printStackTrace();

        }
    }

    // ===== SUPPLIER REGISTRATION EMAIL =====
    public static void sendSupplierRegistrationEmail(String toEmail){

        String subject = "Supplier Registration Received";

        String body =
                "Greetings,\n\n" +
                "Your supplier account has been successfully registered in the Supplier Vendor Management System.\n\n" +
                "Your account is currently under admin review.\n\n" +
                "You will receive another email once your account is approved.\n\n" +
                "Thank you,\n" +
                "Supplier Vendor Management Team";

        sendEmail(toEmail,subject,body);
    }

    // ===== SUPPLIER APPROVAL EMAIL =====
    public static void sendSupplierApprovalEmail(String toEmail, String name){

        String subject = "Supplier Account Approved";

        String body =
                "Hello " + name + ",\n\n" +
                "Congratulations!\n\n" +
                "Your supplier account has been approved by the admin.\n\n" +
                "You can now log in to the system.\n\n" +
                "Best Regards,\n" +
                "Supplier Vendor Management Team";

        sendEmail(toEmail,subject,body);
    }

    // ===== VENDOR REGISTRATION EMAIL =====
    public static void sendRegistrationEmail(String toEmail){

        String subject = "Vendor Registration Successful";

        String body =
                "Greetings,\n\n" +
                "Your vendor account has been successfully registered.\n\n" +
                "Your account is currently under admin review.\n\n" +
                "You will receive another email once approved.\n\n" +
                "Thank you,\n" +
                "Supplier Vendor Management Team";

        sendEmail(toEmail,subject,body);
    }

    // ===== VENDOR APPROVAL EMAIL =====
    public static void sendApprovalEmail(String toEmail,String name){

        String subject = "Vendor Account Approved";

        String body =
                "Hello " + name + ",\n\n" +
                "Congratulations!\n\n" +
                "Your vendor account has been approved by the admin.\n\n" +
                "You may now log in.\n\n" +
                "Best Regards,\n" +
                "Supplier Vendor Management Team";

        sendEmail(toEmail,subject,body);
    }

}
