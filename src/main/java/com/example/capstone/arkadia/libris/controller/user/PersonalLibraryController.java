package com.example.capstone.arkadia.libris.controller.user;

import com.example.capstone.arkadia.libris.dto.request.user.AddToPersonalLibraryRequestDto;
import com.example.capstone.arkadia.libris.dto.response.user.PersonalLIbraryItemDto;
import com.example.capstone.arkadia.libris.dto.response.user.PersonalLibraryDto;
import com.example.capstone.arkadia.libris.enumerated.ReadingStatus;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.service.user.PersonalLibraryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/personal-library")
public class PersonalLibraryController {

    @Autowired
    private PersonalLibraryService personalLibraryService;

    @GetMapping
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<PersonalLibraryDto> viewLibrary(
            @PathVariable Long userId
    ) throws NotFoundException {
        PersonalLibraryDto dto = personalLibraryService.viewLibrary(userId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<PersonalLIbraryItemDto> addItem(
            @PathVariable Long userId,
            @RequestBody @Valid AddToPersonalLibraryRequestDto request
    ) throws NotFoundException {
        PersonalLIbraryItemDto itemDto = personalLibraryService.addItem(userId, request);
        return ResponseEntity.ok(itemDto);
    }

    @PatchMapping("/{productId}/status")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<PersonalLIbraryItemDto> updateStatus(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam ReadingStatus status
    ) throws NotFoundException {
        PersonalLIbraryItemDto itemDto =
                personalLibraryService.updateStatus(userId, productId, status);
        return ResponseEntity.ok(itemDto);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) throws NotFoundException {
        personalLibraryService.removeItem(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<Void> clearLibrary(
            @PathVariable Long userId
    ) throws NotFoundException {
        personalLibraryService.clearLibrary(userId);
        return ResponseEntity.noContent().build();
    }
}
