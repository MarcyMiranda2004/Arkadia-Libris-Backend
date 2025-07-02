package com.example.capstone.arkadia.libris.service;

import com.example.capstone.arkadia.libris.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final String noReplyAddress;

    public SmtpEmailService(JavaMailSender mailSender,
                            @Value("${mail.no-reply}") String noReplyAddress) {
        this.mailSender     = mailSender;
        this.noReplyAddress = noReplyAddress;
    }

    @Override
    public void sendRegistrationConfirmation(User user) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(noReplyAddress);
        msg.setTo(user.getEmail());
        msg.setSubject("Benvenuto in Arkadia Libris!");
        msg.setText("Ciao " + user.getName() + ",\n\n" +
                "Grazie per esserti registrato su Arkadia Libris.\n\n" +
                "— Il team Arkadia Libris");
        mailSender.send(msg);
    }

    @Override
    public void sendPasswordChangedNotice(User user) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(noReplyAddress);
        msg.setTo(user.getEmail());
        msg.setSubject("Arkadia Libris – Password aggiornata");
        msg.setText("Ciao " + user.getName() + ",\n\n" +
                "La tua password è stata aggiornata con successo.\n\n" +
                "— Il team Arkadia Libris");
        mailSender.send(msg);
    }

    @Override
    public void sendEmailChangeConfirmation(User user, String newEmail, String currentEmail) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(noReplyAddress);
        msg.setTo(currentEmail, newEmail);
        msg.setSubject("Arkadia Libris – Email aggiornata");
        msg.setText("Ciao " + user.getName() + ",\n\n" +
                "Il tuo indirizzo email è stato modificato da " + currentEmail + " a " + newEmail +  ".\n\n" +
                "— Il team Arkadia Libris");
        mailSender.send(msg);
    }

    @Override
    public void sendEmailAddressaddedNotice(String email) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(noReplyAddress);
        msg.setTo(email);
        msg.setSubject("Arkadia Libris – Indirizzo aggiunto");
        msg.setText("Hai aggiunto un nuovo indirizzo al tuo profilo.\n\n" +
                "— Il team Arkadia Libris");
        mailSender.send(msg);
    }

    @Override
    public void sendDeleteAccountNotice(User user, String reason) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(noReplyAddress);
        msg.setTo(user.getEmail());
        msg.setSubject("Arkadia Libris – Account eliminato");
        msg.setText("Ciao " + user.getName() + ",\n\n" +
                "Il tuo account è stato eliminato. Motivo: " + reason + ".\n\n" +
                "— Il team Arkadia Libris");
        mailSender.send(msg);
    }
}
