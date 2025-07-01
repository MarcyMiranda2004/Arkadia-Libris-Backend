package com.example.capstone.arkadia.libris.controller;

import com.example.capstone.arkadia.libris.dto.ChangeEmailDto;
import com.example.capstone.arkadia.libris.dto.ChangePasswordDto;
import com.example.capstone.arkadia.libris.dto.UserDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.exception.ValidationException;
import com.example.capstone.arkadia.libris.model.User;
import com.example.capstone.arkadia.libris.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create new user
    @PostMapping
    public ResponseEntity<User> createUser(
            @RequestBody @Valid UserDto userDto,
            BindingResult br) {
        if (br.hasErrors()) {
            throw new ValidationException(br.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .reduce((a, b) -> a + "; " + b).orElse(""));
        }
        User created = userService.saveUser(userDto);
        return ResponseEntity.status(201).body(created);
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUser());
    }

    // Get user by id
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(userService.getUser(id));
    }

    // Update user basic info
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserDto userDto,
            BindingResult br) throws NotFoundException {
        if (br.hasErrors()) {
            throw new ValidationException(br.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .reduce((a, b) -> a + "; " + b).orElse(""));
        }
        User updated = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updated);
    }

    // Update avatar URL
    @PatchMapping("/{id}/avatar")
    public ResponseEntity<User> updateAvatar(
            @PathVariable Long id,
            @RequestParam String avatarUrl) throws NotFoundException {
        User updated = userService.updateUserAvatar(id, avatarUrl);
        return ResponseEntity.ok(updated);
    }

    // Change password
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordDto changePasswordDto,
            BindingResult br) throws NotFoundException {
        if (br.hasErrors()) {
            throw new ValidationException(br.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .reduce((a, b) -> a + "; " + b).orElse(""));
        }
        userService.updateUserPassword(id, changePasswordDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<Void> changeEmail(
            @PathVariable Long id,
            @Valid @RequestBody ChangeEmailDto changeEmailDto,
            BindingResult br) throws NotFoundException {
        if (br.hasErrors()) {
            throw new ValidationException(br.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .reduce((a, b) -> a + "; " + b).orElse(""));
        }
        userService.updateUserEmail(id, changeEmailDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws NotFoundException {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
