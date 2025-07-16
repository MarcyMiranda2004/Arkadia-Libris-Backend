package com.example.capstone.arkadia.libris.service.user;

import com.example.capstone.arkadia.libris.dto.request.user.AddToPersonalLibraryRequestDto;
import com.example.capstone.arkadia.libris.dto.response.user.PersonalLIbraryItemDto;
import com.example.capstone.arkadia.libris.dto.response.user.PersonalLibraryDto;
import com.example.capstone.arkadia.libris.enumerated.ReadingStatus;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.model.user.PersonalLIbrary;
import com.example.capstone.arkadia.libris.model.user.PersonalLibraryItem;
import com.example.capstone.arkadia.libris.model.user.User;
import com.example.capstone.arkadia.libris.model.stock.Product;
import com.example.capstone.arkadia.libris.repository.user.PersonalLibraryItemRepository;
import com.example.capstone.arkadia.libris.repository.user.PersonalLibraryRepository;
import com.example.capstone.arkadia.libris.service.stock.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class PersonalLibraryService {

    @Autowired private PersonalLibraryRepository libraryRepo;
    @Autowired private PersonalLibraryItemRepository itemRepo;
    @Autowired private UserService userService;
    @Autowired private ProductService productService;

    public PersonalLibraryDto viewLibrary(Long userId) throws NotFoundException {
        PersonalLIbrary lib = libraryRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Libreria personale non trovata per utente " + userId));

        PersonalLibraryDto dto = new PersonalLibraryDto();
        dto.setPersonalLibraryId(lib.getId());
        dto.setItems(lib.getItems().stream()
                .map(this::toDto)
                .collect(Collectors.toList()));
        return dto;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PersonalLIbraryItemDto addItem(Long userId, AddToPersonalLibraryRequestDto request) throws NotFoundException {
        User user = userService.getUser(userId);
        Product product = productService.getProductById(request.getProductId());

        PersonalLIbrary library = libraryRepo.findByUserId(userId)
                .orElseGet(() -> {
                    PersonalLIbrary lib = new PersonalLIbrary();
                    lib.setUser(user);
                    return libraryRepo.save(lib);
                });

        PersonalLibraryItem item = itemRepo
                .findByPersonalLibraryIdAndProductId(library.getId(), product.getId())
                .orElseGet(() -> {
                    PersonalLibraryItem newItem = new PersonalLibraryItem();
                    newItem.setPersonalLibrary(library);
                    newItem.setProduct(product);
                    newItem.setStatus(ReadingStatus.DA_LEGGERE);
                    return itemRepo.save(newItem);
                });

        return toDto(item);
    }

    @Transactional
    public PersonalLIbraryItemDto updateStatus(Long userId, Long productId, ReadingStatus status) throws NotFoundException {
        PersonalLIbrary lib = libraryRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Libreria personale non trovata"));
        PersonalLibraryItem item = itemRepo
                .findByPersonalLibraryIdAndProductId(lib.getId(), productId)
                .orElseThrow(() -> new NotFoundException("Prodotto non in libreria: " + productId));
        item.setStatus(status);
        itemRepo.save(item);
        return toDto(item);
    }

    @Transactional
    public void removeItem(Long userId, Long productId) throws NotFoundException {
        PersonalLIbrary lib = libraryRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Libreria personale non trovata"));
        itemRepo.deleteByPersonalLibraryIdAndProductId(lib.getId(), productId);
    }

    @Transactional
    public void clearLibrary(Long userId) throws NotFoundException {
        PersonalLIbrary lib = libraryRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Libreria personale non trovata"));
        lib.getItems().clear();
        libraryRepo.save(lib);
    }

    private PersonalLIbraryItemDto toDto(PersonalLibraryItem i) {
        PersonalLIbraryItemDto d = new PersonalLIbraryItemDto();
        d.setProductId(i.getProduct().getId());
        d.setProductName(i.getProduct().getTitle());
        d.setStatus(i.getStatus());
        return d;
    }
}
