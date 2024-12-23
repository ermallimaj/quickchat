package com.example.quickchat.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.quickchat.R;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    public static void sendEmail(Context context, String toEmail, String subject, String messageBody) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            boolean success;
            String email = context.getString(R.string.email);
            String password = context.getString(R.string.password);

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Properties props = new Properties();
                    props.put("mail.smtp.host", "smtp.gmail.com");
                    props.put("mail.smtp.port", "587");
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", "true");

                    Session session = Session.getInstance(props, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(email, password);
                        }
                    });

                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(email));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                    message.setSubject(subject);
                    message.setText(messageBody);

                    Transport.send(message);
                    success = true;
                } catch (Exception e) {
                    success = false;
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (success) {
                    Toast.makeText(context, "Email sent successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to send email", Toast.LENGTH_SHORT).show();
                }
            }
        };

        task.execute();
    }
}
