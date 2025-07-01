package com.example.capstone.arkadia.libris.service;

import com.cloudinary.Cloudinary;
import com.example.capstone.arkadia.libris.dto.ChangeEmailDto;
import com.example.capstone.arkadia.libris.dto.ChangePasswordDto;
import com.example.capstone.arkadia.libris.dto.UserDto;
import com.example.capstone.arkadia.libris.enumerated.Role;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.model.User;
import com.example.capstone.arkadia.libris.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public User saveUser(UserDto userDto){
        User u = new User();
        u.setName(userDto.getName());
        u.setSurname(userDto.getSurname());
        u.setBornDate(userDto.getBornDate());
        u.setUsername(userDto.getUsername());
        u.setEmail(userDto.getEmail());
        u.setPassword(passwordEncoder.encode(userDto.getPassword()));
        u.setAvatarUrl("https://ui-avatars.com/api/?name=" + u.getName().charAt(0) + "+" + u.getSurname().charAt(0));
        u.setRole(Role.USER);

        User savedUser = userRepository.save(u);
        emailService.sendRegistrationConfirmation(savedUser);
        return savedUser;
    }

    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    public User getUser(Long id) throws NotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User con id " + id + " non trovato"));
    }

    public User updateUser(Long id, UserDto userDto) throws NotFoundException {
        User u = getUser(id);
        u.setName(userDto.getName());
        u.setSurname(userDto.getSurname());
        u.setUsername(userDto.getUsername());
        u.setBornDate(userDto.getBornDate());
        u.setPhoneNumber(userDto.getPhoneNumber());
        return userRepository.save(u);
    }

    public User updateUserAvatar(Long id, String avatarUrl) throws NotFoundException {
        User u = getUser(id);
        u.setAvatarUrl(avatarUrl);
        return userRepository.save(u);
    }

    @Transactional
    public void updateUserPassword(Long id, ChangePasswordDto changePasswordDto) throws NotFoundException {
        User u = getUser(id);
        if(!passwordEncoder.matches(changePasswordDto.getOldPassword(), u.getPassword())) {
            throw new BadCredentialsException("La vecchia password non corrisponde");
        }
        if(!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword())) {
            throw new BadCredentialsException("La nuova password e la conferma non corrispondono");
        }
        u.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(u);
        emailService.sendPasswordChangedNotice(u);
    }

    @Transactional
    public void updateUserEmail(Long id, ChangeEmailDto dto) throws NotFoundException {
        User u = getUser(id);
        if (!passwordEncoder.matches(dto.getPassword(), u.getPassword())) {
            throw new BadCredentialsException("La password non corrisponde");
        }
        if (!u.getEmail().equals(dto.getCurrentEmail())) {
            throw new BadCredentialsException("Inserisci l'email corrente corretta");
        }
        if (!dto.getNewEmail().equals(dto.getConfirmNewEmail())) {
            throw new BadCredentialsException("La nuova email e la conferma non corrispondono");
        }
        u.setEmail(dto.getNewEmail());
        userRepository.save(u);
        emailService.sendEmailChangeConfirmation(u, dto.getNewEmail());
    }

    public void deleteUser(Long id) throws NotFoundException {
        User u = getUser(id);
        userRepository.delete(u);
    }
}
