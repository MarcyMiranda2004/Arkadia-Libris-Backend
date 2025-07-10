package com.example.capstone.arkadia.libris.dto.request.administration;

import com.example.capstone.arkadia.libris.enumerated.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignRoleRequestDto {

    @NotNull
    private Role role;
}
