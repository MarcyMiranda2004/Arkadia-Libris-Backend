package com.example.capstone.arkadia.libris.service.user;

import com.cloudinary.Cloudinary;
import com.example.capstone.arkadia.libris.dto.request.administration.AssignRoleRequestDto;
import com.example.capstone.arkadia.libris.dto.request.administration.CreateStaffRequestDto;
import com.example.capstone.arkadia.libris.dto.response.user.UserDto;
import com.example.capstone.arkadia.libris.dto.request.user.ChangeEmailDto;
import com.example.capstone.arkadia.libris.dto.request.user.ChangePasswordDto;
import com.example.capstone.arkadia.libris.dto.request.user.UpdateUserDto;
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

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private WishlistRepository wishlistRepository;
    @Autowired private PersonalLibraryRepository personalLibraryRepository;
    @Autowired private Cloudinary cloudinary;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EmailService emailService;

    public User saveUser(UserDto userDto) {
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

        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);
        savedUser.setCart(cart);

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(savedUser);
        wishlistRepository.save(wishlist);
        savedUser.setWishlist(wishlist);

        PersonalLIbrary personalLIbrary = new PersonalLIbrary();
        personalLIbrary.setUser(savedUser);
        personalLibraryRepository.save(personalLIbrary);
        savedUser.setPersonalLibrary(personalLIbrary);

        return savedUser;
    }

    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    public User getUser(Long id) throws NotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User con id " + id + " non trovato"));
    }

    public User getUserByEmail(String email) throws NotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User con email " + email + " non trovato"));
    }

    public User getUserByUsername(String username) throws NotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User con username " + username + " non trovato"));
    }

    public User updateUser(Long id, UpdateUserDto updateUserDto) throws NotFoundException {
        User u = getUser(id);
        u.setName(updateUserDto.getName());
        u.setSurname(updateUserDto.getSurname());
        u.setUsername(updateUserDto.getUsername());
        u.setBornDate(updateUserDto.getBornDate());
        u.setPhoneNumber(updateUserDto.getPhoneNumber());
        return userRepository.save(u);
    }

    public String updateUserAvatar(Long id, MultipartFile avatarUrl) throws NotFoundException, IOException {
        User u = getUser(id);
        String url = (String) cloudinary.uploader()
                .upload(avatarUrl.getBytes(), Collections.emptyMap())
                .get("url");
        u.setAvatarUrl(url);
        userRepository.save(u);
        return url;
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
    public void updateUserEmail(Long id, ChangeEmailDto changeEmailDto) throws NotFoundException {
        User u = getUser(id);
        if (!passwordEncoder.matches(changeEmailDto.getPassword(), u.getPassword())) {
            throw new BadCredentialsException("La password non corrisponde");
        }
        if (!u.getEmail().equals(changeEmailDto.getCurrentEmail())) {
            throw new BadCredentialsException("Inserisci l'email corrente corretta");
        }
        if (!changeEmailDto.getNewEmail().equals(changeEmailDto.getConfirmNewEmail())) {
            throw new BadCredentialsException("La nuova email e la conferma non corrispondono");
        }
        u.setEmail(changeEmailDto.getNewEmail());
        userRepository.save(u);
        emailService.sendEmailChangeConfirmation(u,changeEmailDto.getNewEmail(), changeEmailDto.getCurrentEmail());
    }

    @Transactional
    public void deleteUser(Long id, String rawPassword) throws NotFoundException {
        User u = getUser(id);
        if (!passwordEncoder.matches(rawPassword, u.getPassword())) {
            throw new BadCredentialsException("Password non corretta");
        }
        userRepository.delete(u);
        emailService.sendDeleteAccountNotice(u, "Account Eliminato su richiesta dell'utente");
    }

    public UserDto createUserWithRole(CreateStaffRequestDto createStaffRequestDto) {
        User u = new User();
        u.setName(createStaffRequestDto.getName());
        u.setSurname(createStaffRequestDto.getSurname());
        u.setBornDate(createStaffRequestDto.getBornDate());
        u.setUsername(createStaffRequestDto.getUsername());
        u.setEmail(createStaffRequestDto.getEmail());
        u.setPassword(passwordEncoder.encode(createStaffRequestDto.getPassword()));
        u.setAvatarUrl("https://ui-avatars.com/api/?name=" + u.getName().charAt(0) + "+" + u.getSurname().charAt(0));
        u.setRole(createStaffRequestDto.getRole());

        User saved = userRepository.save(u);
        emailService.sendRegistrationConfirmation(saved);

        Cart cart = new Cart(); cart.setUser(saved);
        cartRepository.save(cart); saved.setCart(cart);

        Wishlist wl = new Wishlist(); wl.setUser(saved);
        wishlistRepository.save(wl); saved.setWishlist(wl);

        PersonalLIbrary lib = new PersonalLIbrary(); lib.setUser(saved);
        personalLibraryRepository.save(lib); saved.setPersonalLibrary(lib);

        UserDto out = new UserDto();
        out.setName(saved.getName());
        out.setSurname(saved.getSurname());
        out.setBornDate(saved.getBornDate());
        out.setPhoneNumber(saved.getPhoneNumber());
        out.setUsername(saved.getUsername());
        out.setEmail(saved.getEmail());
        out.setAvatarUrl(saved.getAvatarUrl());
        out.setPassword("");
        out.setRole(saved.getRole());
        return out;
    }

    @Transactional
    public void assignRole(Long userId, AssignRoleRequestDto req) throws NotFoundException {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User non trovato: " + userId));
        u.setRole(req.getRole());
        userRepository.save(u);
    }
}
