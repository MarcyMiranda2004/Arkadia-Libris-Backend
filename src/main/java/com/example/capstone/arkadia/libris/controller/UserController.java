package com.example.capstone.arkadia.libris.controller;

import com.example.capstone.arkadia.libris.dto.ChangeEmailDto;
import com.example.capstone.arkadia.libris.dto.ChangePasswordDto;
import com.example.capstone.arkadia.libris.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {this.userService = userService;}

    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @Valid
            @RequestBody
            ChangePasswordDto changePasswordDto
    ) {
        userService.updateUserPassword(id, changePasswordDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<?> changeEmail(
            @PathVariable Long id,
            @Valid
            @RequestBody
            ChangeEmailDto changeEmailDto
    ) {
        userService.updateUserEmail(id, changeEmailDto);
        return ResponseEntity.noContent().build();
    }
}

