// src/main/java/com/example/capstone/arkadia/libris/controller/user/UserController.java
package com.example.capstone.arkadia.libris.controller.user;

import com.example.capstone.arkadia.libris.dto.request.user.DeleteUserDto;
import com.example.capstone.arkadia.libris.dto.response.user.UserDto;
import com.example.capstone.arkadia.libris.dto.request.user.ChangeEmailDto;
import com.example.capstone.arkadia.libris.dto.request.user.ChangePasswordDto;
import com.example.capstone.arkadia.libris.dto.request.user.UpdateUserDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.exception.ValidationException;
import com.example.capstone.arkadia.libris.model.user.User;
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
    public UserController(UserService userService) { this.userService = userService; }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto dto, BindingResult br) {
        if (br.hasErrors()) throw new ValidationException(
                br.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.joining("; "))
        );
        return ResponseEntity.status(201).body(userService.saveUserDto(dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUser());
    }

    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/dto")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<UserDto>> getAllUserDtos() {
        return ResponseEntity.ok(userService.getAllUserDto());
    }

    @GetMapping("/dto/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<UserDto> getUserDtoById(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(userService.getUserDto(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserDto dto,
            BindingResult br
    ) throws NotFoundException {
        if (br.hasErrors()) {
            throw new ValidationException(
                    br.getAllErrors().stream()
                            .map(e -> e.getDefaultMessage())
                            .collect(Collectors.joining("; "))
            );
        }
        User updatedUser = userService.updateUser(id, dto);
        UserDto updatedDto = userService.getUserDto(id);
        return ResponseEntity.ok(updatedDto);
    }

    @PatchMapping(path = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<String> updateAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file)
            throws NotFoundException, IOException {
        return ResponseEntity.ok(userService.updateUserAvatar(id, file));
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN') or hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordDto dto,
            BindingResult br
    ) throws NotFoundException {
        if (br.hasErrors()) throw new ValidationException(
                br.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.joining("; "))
        );
        userService.updateUserPassword(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/email")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN') or hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<Void> changeEmail(
            @PathVariable Long id,
            @Valid @RequestBody ChangeEmailDto dto,
            BindingResult br
    ) throws NotFoundException {
        if (br.hasErrors()) throw new ValidationException(
                br.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.joining("; "))
        );
        userService.updateUserEmail(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @RequestBody @Valid DeleteUserDto dto,
            BindingResult br
    ) throws NotFoundException {
        if (br.hasErrors()) throw new ValidationException(
                br.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.joining("; "))
        );
        userService.deleteUser(id, dto.getPassword());
        return ResponseEntity.noContent().build();
    }
}
