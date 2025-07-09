package com.example.capstone.arkadia.libris.security.forgot;

import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.exception.ValidationException;
import com.example.capstone.arkadia.libris.model.user.User;
import com.example.capstone.arkadia.libris.repository.user.UserRepository;
import com.example.capstone.arkadia.libris.service.notification.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {
    @Autowired
    private VerificationTokenRepository tokenRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private EmailService emailService;
    @Autowired private PasswordEncoder passwordEncoder;

    public void createPasswordResetToken(String email) {
        User u = userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("Email non registrata"));
        tokenRepo.deleteByUser(u);
        String token = UUID.randomUUID().toString();
        VerificationToken vt = new VerificationToken();
        vt.setToken(token);
        vt.setUser(u);
        vt.setExpiresAt(LocalDateTime.now().plusHours(1));
        tokenRepo.save(vt);
        emailService.sendPasswordReset(u, token);
    }

    public void resetPassword(String token, String newPassword) {
        VerificationToken vt = tokenRepo.findByToken(token)
                .orElseThrow(() -> new ValidationException("Token non valido"));
        if (vt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Token scaduto");
        }
        User u = vt.getUser();
        u.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(u);
        tokenRepo.delete(vt);
    }
}

