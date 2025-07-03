package com.example.capstone.arkadia.libris.controller;

import com.example.capstone.arkadia.libris.dto.request.DeleteUserDto;
import com.example.capstone.arkadia.libris.dto.response.UserDto;
import com.example.capstone.arkadia.libris.dto.request.ChangeEmailDto;
import com.example.capstone.arkadia.libris.dto.request.ChangePasswordDto;
import com.example.capstone.arkadia.libris.dto.request.UpdateUserDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.exception.ValidationException;
import com.example.capstone.arkadia.libris.model.User;
import com.example.capstone.arkadia.libris.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {this.userService = userService;}

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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<List<User>> getAllUsers() {return ResponseEntity.ok(userService.getAllUser());}

    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN') or hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize(("#email == authentication.principal.email or hasRole('ADMIN') or hasRole('CUSTOMER_SERVICE')"))
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) throws NotFoundException {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/username/{Username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String Username) throws NotFoundException {
        return ResponseEntity.ok(userService.getUserByUsername(Username));
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody @Valid UpdateUserDto updateUserDto,
            BindingResult br) throws NotFoundException {
        if (br.hasErrors()) {
            throw new ValidationException(br.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .reduce((a, b) -> a + "; " + b).orElse(""));
        }
        User updated = userService.updateUser(id, updateUserDto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping(path = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<String> updateAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file
    ) throws NotFoundException, IOException {
        String updated = userService.updateUserAvatar(id, file);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN') or hasRole('CUSTOMER_SERVICE')")
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
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN') or hasRole('CUSTOMER_SERVICE')")
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
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @RequestBody @Valid DeleteUserDto deleteUserDto,
            BindingResult br
    ) throws NotFoundException {
        if (br.hasErrors()) {
            String errs = br.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            throw new ValidationException(errs);
        }
        userService.deleteUser(id, deleteUserDto.getPassword());
        return ResponseEntity.noContent().build();
    }
}
