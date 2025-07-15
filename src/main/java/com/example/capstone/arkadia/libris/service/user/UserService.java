// src/main/java/com/example/capstone/arkadia/libris/service/user/UserService.java
package com.example.capstone.arkadia.libris.service.user;

import com.cloudinary.Cloudinary;
import com.example.capstone.arkadia.libris.dto.request.administration.AssignRoleRequestDto;
import com.example.capstone.arkadia.libris.dto.request.administration.CreateStaffRequestDto;
import com.example.capstone.arkadia.libris.dto.request.user.ChangeEmailDto;
import com.example.capstone.arkadia.libris.dto.request.user.ChangePasswordDto;
import com.example.capstone.arkadia.libris.dto.request.user.UpdateUserDto;
import com.example.capstone.arkadia.libris.dto.response.user.UserDto;
import com.example.capstone.arkadia.libris.enumerated.Role;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.model.purchase.Cart;
import com.example.capstone.arkadia.libris.model.user.PersonalLIbrary;
import com.example.capstone.arkadia.libris.model.user.User;
import com.example.capstone.arkadia.libris.model.user.Wishlist;
import com.example.capstone.arkadia.libris.repository.purchase.CartRepository;
import com.example.capstone.arkadia.libris.repository.user.PersonalLibraryRepository;
import com.example.capstone.arkadia.libris.repository.user.UserRepository;
import com.example.capstone.arkadia.libris.repository.user.WishlistRepository;
import com.example.capstone.arkadia.libris.service.notification.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired private UserRepository userRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private WishlistRepository wishlistRepository;
    @Autowired private PersonalLibraryRepository personalLibraryRepository;
    @Autowired private Cloudinary cloudinary;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EmailService emailService;

    public User saveUser(UserDto dto) {
        User u = new User();
        u.setName(dto.getName());
        u.setSurname(dto.getSurname());
        u.setBornDate(dto.getBornDate());
        u.setUsername(dto.getUsername());
        u.setEmail(dto.getEmail());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setAvatarUrl("https://ui-avatars.com/api/?name=" +
                u.getName().charAt(0) + "+" + u.getSurname().charAt(0));
        u.setRole(Role.USER);
        User saved = userRepository.save(u);

        Cart cart = new Cart(); cart.setUser(saved); cartRepository.save(cart); saved.setCart(cart);
        Wishlist wl = new Wishlist(); wl.setUser(saved); wishlistRepository.save(wl); saved.setWishlist(wl);
        PersonalLIbrary lib = new PersonalLIbrary(); lib.setUser(saved); personalLibraryRepository.save(lib); saved.setPersonalLibrary(lib);

        emailService.sendRegistrationConfirmation(saved);
        return saved;
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public User getUser(Long id) throws NotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User " + id + " non trovato"));
    }

    public User getUserByEmail(String email) throws NotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User con email " + email + " non trovato"));
    }

    public User getUserByUsername(String username) throws NotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User con username " + username + " non trovato"));
    }

    public User updateUser(Long id, UpdateUserDto dto) throws NotFoundException {
        User u = getUser(id);
        u.setName(dto.getName());
        u.setSurname(dto.getSurname());
        u.setUsername(dto.getUsername());
        u.setBornDate(dto.getBornDate());
        u.setPhoneNumber(dto.getPhoneNumber());
        return userRepository.save(u);
    }

    public String updateUserAvatar(Long id, MultipartFile file) throws NotFoundException, IOException {
        User u = getUser(id);
        String url = (String) cloudinary.uploader()
                .upload(file.getBytes(), Collections.emptyMap())
                .get("url");
        u.setAvatarUrl(url);
        userRepository.save(u);
        return url;
    }

    @Transactional
    public void updateUserPassword(Long id, ChangePasswordDto dto) throws NotFoundException {
        User u = getUser(id);
        if (!passwordEncoder.matches(dto.getOldPassword(), u.getPassword()))
            throw new BadCredentialsException("La vecchia password non corrisponde");
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword()))
            throw new BadCredentialsException("La nuova password e la conferma non corrispondono");
        u.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(u);
        emailService.sendPasswordChangedNotice(u);
    }

    @Transactional
    public void updateUserEmail(Long id, ChangeEmailDto dto) throws NotFoundException {
        User u = getUser(id);
        if (!passwordEncoder.matches(dto.getPassword(), u.getPassword()))
            throw new BadCredentialsException("La password non corrisponde");
        if (!u.getEmail().equals(dto.getCurrentEmail()))
            throw new BadCredentialsException("Inserisci l'email corrente corretta");
        if (!dto.getNewEmail().equals(dto.getConfirmNewEmail()))
            throw new BadCredentialsException("La nuova email e la conferma non corrispondono");
        u.setEmail(dto.getNewEmail());
        userRepository.save(u);
        emailService.sendEmailChangeConfirmation(u, dto.getNewEmail(), dto.getCurrentEmail());
    }

    @Transactional
    public void deleteUser(Long id, String rawPassword) throws NotFoundException {
        User u = getUser(id);
        if (!passwordEncoder.matches(rawPassword, u.getPassword()))
            throw new BadCredentialsException("Password non corretta");
        userRepository.delete(u);
        emailService.sendDeleteAccountNotice(u, "Account eliminato");
    }

    private UserDto toDto(User u) {
        UserDto dto = new UserDto();
        dto.setName(u.getName());
        dto.setSurname(u.getSurname());
        dto.setBornDate(u.getBornDate());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        dto.setPhoneNumber(u.getPhoneNumber());
        dto.setAvatarUrl(u.getAvatarUrl());
        dto.setRole(u.getRole());
        return dto;
    }

    public UserDto saveUserDto(UserDto dto) {
        return toDto(saveUser(dto));
    }

    public List<UserDto> getAllUserDto() {
        return getAllUser().stream().map(this::toDto).collect(Collectors.toList());
    }

    public UserDto getUserDto(Long id) throws NotFoundException {
        return toDto(getUser(id));
    }

    public UserDto updateUserDto(Long id, UpdateUserDto dto) throws NotFoundException {
        return toDto(updateUser(id, dto));
    }

    public UserDto createUserWithRole(CreateStaffRequestDto req) {
        User u = new User();
        u.setName(req.getName());
        u.setSurname(req.getSurname());
        u.setBornDate(req.getBornDate());
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setAvatarUrl("https://ui-avatars.com/api/?name=" +
                u.getName().charAt(0) + "+" + u.getSurname().charAt(0));
        u.setRole(req.getRole());
        User saved = userRepository.save(u);

        Cart cart = new Cart(); cart.setUser(saved); cartRepository.save(cart); saved.setCart(cart);
        Wishlist wl = new Wishlist(); wl.setUser(saved); wishlistRepository.save(wl); saved.setWishlist(wl);
        PersonalLIbrary lib = new PersonalLIbrary(); lib.setUser(saved); personalLibraryRepository.save(lib); saved.setPersonalLibrary(lib);

        emailService.sendRegistrationConfirmation(saved);
        return toDto(saved);
    }

    @Transactional
    public void assignRole(Long userId, AssignRoleRequestDto req) throws NotFoundException {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User non trovato: " + userId));
        u.setRole(req.getRole());
        userRepository.save(u);
    }
}
