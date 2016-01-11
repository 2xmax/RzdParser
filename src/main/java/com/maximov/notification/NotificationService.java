package com.maximov.notification;

import com.maximov.data.Train;
import com.maximov.data.TrainFilter;
import com.maximov.data.TrainSearchResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;


/**
 * Maxim Maximov, 2013
 * 2xmax@mail.ru
 * MSc, 2nd year
 * St Petersburg State University
 * Physics Faculty
 * Department of Computational Physics
 */

public class NotificationService implements INotificationService {
    private final Log log = LogFactory.getLog(NotificationService.class);
    private final long ServiceIsAvailableNotificationTimeoutMs;
    private final Properties properties;
    private long lastNotifiedMillis = System.currentTimeMillis();

    public NotificationService() throws IOException {
        properties = new Properties();
        properties.load(ClassLoader.getSystemResourceAsStream("notifications.properties"));
        ServiceIsAvailableNotificationTimeoutMs = Long.parseLong(properties.getProperty("notifications.statusupdatetimeoutms"));
    }

    @Override
    public void notifySuccess(TrainFilter filter, TrainSearchResult result) {
        sendEmail("Tickets have been found!", composeMessage(filter, result));
    }

    @Override
    public void notifyServiceIsAvailable() {
        if (canNotify()) {
            sendEmail("Service is working", "Service is working");
        }
    }

    @Override
    public void notifyServiceIsDown() {
        if (canNotify()) {
            sendEmail("Service is down", "Service is down");
        }
    }

    private boolean canNotify() {
        if (lastNotifiedMillis + ServiceIsAvailableNotificationTimeoutMs < System.currentTimeMillis()) {
            lastNotifiedMillis = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    private void sendEmail(String subject, String body) {
        log.info("sending mail: subj=" + subject + "; body=" + body);
        final String username = properties.getProperty("notifications.email.smtp.username");
        final String password = properties.getProperty("notifications.email.smtp.password");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", properties.getProperty("notifications.email.smtp.ssl"));
        props.put("mail.smtp.host", properties.getProperty("notifications.email.smtp.host"));
        props.put("mail.smtp.port", properties.getProperty("notifications.email.smtp.port"));

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username, "RZD parser"));
            String targetMailbox = properties.getProperty("notifications.email.to");
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(targetMailbox, targetMailbox));
            msg.setSubject("[RZD] " + subject);
            msg.setText(body);
            Transport.send(msg);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private String composeMessage(TrainFilter filter, TrainSearchResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("Query:" + filter + "\r\n");
        sb.append("=====================\r\n");
        for (Train item : result.getItems()) {
            sb.append(item.toString());
            sb.append("\r\n");
        }
        return sb.toString();
    }
}
