package com.example.capstone.arkadia.libris.service.notification;

import com.example.capstone.arkadia.libris.model.user.User;

public interface EmailService {
    void sendRegistrationConfirmation(User user);

    void sendPasswordChangedNotice(User user);

    void sendEmailChangeConfirmation(User user, String currentEmail, String newEmail );

    void sendEmailAddressaddedNotice(String email);

    void sendDeleteAccountNotice(User user, String reason);

    void sendOrderConfirmation(User user, Long orderId, double totalAmount);
}
