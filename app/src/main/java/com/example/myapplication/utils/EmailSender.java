package com.example.myapplication.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {

    public static void sendEmail(Context context, String toEmail, String subject, String messageBody) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            boolean success;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // Configure email properties
                    Properties props = new Properties();
                    props.put("mail.smtp.host", "smtp.gmail.com");
                    props.put("mail.smtp.socketFactory.port", "465");
                    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.port", "465");

                    // Create session
                    Session session = Session.getDefaultInstance(props,
                            new javax.mail.Authenticator() {
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    // Replace with your email and app-specific password
                                    return new PasswordAuthentication("youremail@gmail.com", "yourpassword");
                                }
                            });

                    // Create message
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress("youremail@gmail.com"));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                    message.setSubject(subject);
                    message.setText(messageBody);

                    // Send email
                    Transport.send(message);
                    success = true;
                } catch (MessagingException e) {
                    success = false;
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (success) {
                    Toast.makeText(context, "Email sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to send email", Toast.LENGTH_SHORT).show();
                }
            }
        };

        task.execute();
    }
}
