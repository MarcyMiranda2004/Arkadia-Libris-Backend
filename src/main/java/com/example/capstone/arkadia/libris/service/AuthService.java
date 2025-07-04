package com.example.capstone.arkadia.libris.service;

import com.example.capstone.arkadia.libris.dto.LoginDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.model.User;
import com.example.capstone.arkadia.libris.repository.UserRepository;
import com.example.capstone.arkadia.libris.security.JwtTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTool jwtTool;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String login(LoginDto loginDto) throws NotFoundException {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new NotFoundException("Utente con questa email/password non trovato"));
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new NotFoundException("Utente con questa email/password non trovato");
        }
        return jwtTool.createToken(user);
    }
}
