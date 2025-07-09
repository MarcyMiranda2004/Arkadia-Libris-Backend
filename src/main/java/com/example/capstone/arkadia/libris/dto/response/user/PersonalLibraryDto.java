package com.example.capstone.arkadia.libris.dto.response.user;

import lombok.Data;

import java.util.List;

@Data
public class PersonalLibraryDto {
    private Long personalLibraryId;
    private List<PersonalLIbraryItemDto> items;
}
