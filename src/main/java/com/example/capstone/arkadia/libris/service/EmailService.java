package com.example.capstone.arkadia.libris.service;

import com.example.capstone.arkadia.libris.model.User;

public interface EmailService {
    void sendRegistrationConfirmation(User user);

    void sendPasswordChangedNotice(User user);

    void sendEmailChangeConfirmation(User user, String newEmail);
}
