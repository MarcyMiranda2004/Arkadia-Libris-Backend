package com.example.capstone.arkadia.libris.security.forgot;

import com.example.capstone.arkadia.libris.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    void deleteByUser(User user);
}

