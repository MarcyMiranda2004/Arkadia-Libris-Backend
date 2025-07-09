package com.example.capstone.arkadia.libris.dto.response.user;

import com.example.capstone.arkadia.libris.enumerated.ReadingStatus;
import lombok.Data;

@Data
public class PersonalLIbraryItemDto {
    private Long productId;
    private String productName;
    private ReadingStatus status;
}
