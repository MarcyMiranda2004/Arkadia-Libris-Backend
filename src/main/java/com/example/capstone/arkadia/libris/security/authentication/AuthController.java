package com.example.capstone.arkadia.libris.security.authentication;


import com.example.capstone.arkadia.libris.dto.response.LoginDto;
import com.example.capstone.arkadia.libris.dto.response.user.UserDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.exception.ValidationException;
import com.example.capstone.arkadia.libris.model.user.User;
import com.example.capstone.arkadia.libris.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public User register(@RequestBody @Validated UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getAllErrors().stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .reduce("", (s, e) -> s + e));
        }
        return userService.saveUser(userDto);
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestBody @Validated LoginDto loginDto, BindingResult bindingResult
    ) throws ValidationException, NotFoundException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(
                    bindingResult.getAllErrors().stream()
                            .map(objectError -> objectError.getDefaultMessage())
                            .reduce("", (s, e) -> s + e)
            );
        }

        String token = authService.login(loginDto);
        User user = userService.getUserByEmail(loginDto.getEmail());
        Long userId = user.getId();
        LoginResponse response = new LoginResponse(token, userId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

}

