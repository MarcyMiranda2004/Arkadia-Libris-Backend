package com.example.capstone.arkadia.libris.controller.administration;

import com.example.capstone.arkadia.libris.dto.request.administration.AssignRoleRequestDto;
import com.example.capstone.arkadia.libris.dto.request.administration.CreateStaffRequestDto;
import com.example.capstone.arkadia.libris.dto.response.user.UserDto;
import com.example.capstone.arkadia.libris.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired private UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createStaff(@RequestBody @Valid CreateStaffRequestDto dto) {
        UserDto created = userService.createUserWithRole(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<Void> assignRole(
            @PathVariable Long userId,
            @RequestBody @Valid AssignRoleRequestDto dto
    ) {
        userService.assignRole(userId, dto);
        return ResponseEntity.noContent().build();
    }
}
