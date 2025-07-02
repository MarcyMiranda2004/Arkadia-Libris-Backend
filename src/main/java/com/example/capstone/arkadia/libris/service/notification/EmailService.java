package com.example.capstone.arkadia.libris.service.notification;

import com.example.capstone.arkadia.libris.model.User;

public interface EmailService {
    void sendRegistrationConfirmation(User user);

    void sendPasswordChangedNotice(User user);

    void sendEmailChangeConfirmation(User user, String currentEmail, String newEmail );

    void sendEmailAddressaddedNotice(String email);

    void sendDeleteAccountNotice(User user, String reason);
}
