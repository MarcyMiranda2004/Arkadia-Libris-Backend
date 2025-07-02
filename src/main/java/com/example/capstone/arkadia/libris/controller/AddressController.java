package com.example.capstone.arkadia.libris.controller;

import com.example.capstone.arkadia.libris.dto.AddressDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.exception.ValidationException;
import com.example.capstone.arkadia.libris.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/{userId}/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    @PreAuthorize("#userId == authentication.principal.id ")
    public ResponseEntity<List<AddressDto>> list(@PathVariable Long userId) throws NotFoundException {
        return ResponseEntity.ok(addressService.listAddresses(userId));
    }

    @PostMapping
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<AddressDto> create(
            @PathVariable("userId") Long userId,
            @RequestBody @Valid AddressDto addressDto,
            BindingResult br
    ) throws NotFoundException {
        if (br.hasErrors()) {
            String errs = br.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            throw new ValidationException(errs);
        }
        AddressDto created = addressService.saveAddress(userId, addressDto);
        return ResponseEntity.status(201).body(created);
    }


    @PutMapping("/{addressId}")
    @PreAuthorize("#userId == authentication.principal.id ")
    public ResponseEntity<AddressDto> update(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @RequestBody @Valid AddressDto addressDto,
            BindingResult br
    ) throws NotFoundException, AccessDeniedException {
        if (br.hasErrors()) {
            String errs = br.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            throw new ValidationException(errs);
        }
        AddressDto updated = addressService.updateAddress(userId, addressId, addressDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{addressId}")
    @PreAuthorize("#userId == authentication.principal.id ")
    public ResponseEntity<Void> delete(
            @PathVariable Long userId,
            @PathVariable Long addressId
    ) throws NotFoundException, AccessDeniedException {
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }
}
