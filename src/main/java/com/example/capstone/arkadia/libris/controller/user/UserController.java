// src/main/java/com/example/capstone/arkadia/libris/controller/user/UserController.java
package com.example.capstone.arkadia.libris.controller.user;

import com.example.capstone.arkadia.libris.dto.request.user.*;
import com.example.capstone.arkadia.libris.dto.response.user.UserDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.exception.ValidationException;
import com.example.capstone.arkadia.libris.model.user.User;
import com.example.capstone.arkadia.libris.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/dto/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @RequestParam(required = false, defaultValue = "") String query,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<UserDto> page = userService.searchUserDtos(query.trim(), pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserDto updateUserDto,
            BindingResult br
    ) throws NotFoundException {
        if (br.hasErrors()) {
            throw new ValidationException(
                    br.getAllErrors().stream()
                            .map(e -> e.getDefaultMessage())
                            .collect(Collectors.joining("; "))
            );
        }
        User updatedUser = userService.updateUser(id, updateUserDto);
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
            @Valid @RequestBody ChangePasswordDto changePasswordDto,
            BindingResult br
    ) throws NotFoundException {
        if (br.hasErrors()) throw new ValidationException(
                br.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.joining("; "))
        );
        userService.updateUserPassword(id, changePasswordDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/email")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN') or hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<Void> changeEmail(
            @PathVariable Long id,
            @Valid @RequestBody ChangeEmailDto changeEmailDto,
            BindingResult br
    ) throws NotFoundException {
        if (br.hasErrors()) throw new ValidationException(
                br.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.joining("; "))
        );
        userService.updateUserEmail(id, changeEmailDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/phone")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<Void> changePhone(
            @PathVariable Long id,
            @Valid @RequestBody ChangePhoneNumberDto changePhoneNumberDto,
            BindingResult br
    ) throws NotFoundException {
        if (br.hasErrors()) {
            throw new ValidationException(
                    br.getAllErrors()
                            .stream()
                            .map(e -> e.getDefaultMessage())
                            .collect(Collectors.joining("; "))
            );
        }
        userService.updateUserPhone(id, changePhoneNumberDto.getPhoneNumber());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @RequestBody @Valid DeleteUserDto deleteUserDto,
            BindingResult br
    ) throws NotFoundException {
        if (br.hasErrors()) throw new ValidationException(
                br.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.joining("; "))
        );
        userService.deleteUser(id, deleteUserDto.getPassword());
        return ResponseEntity.noContent().build();
    }
}
