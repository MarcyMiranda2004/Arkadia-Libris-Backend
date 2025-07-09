package com.example.capstone.arkadia.libris.service.user;

import com.example.capstone.arkadia.libris.dto.request.user.AddToPersonalLibraryRequestDto;
import com.example.capstone.arkadia.libris.dto.response.user.PersonalLIbraryItemDto;
import com.example.capstone.arkadia.libris.dto.response.user.PersonalLibraryDto;
import com.example.capstone.arkadia.libris.enumerated.ReadingStatus;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.model.user.PersonalLIbrary;
import com.example.capstone.arkadia.libris.model.user.PersonalLibraryItem;
import com.example.capstone.arkadia.libris.model.stock.Product;
import com.example.capstone.arkadia.libris.repository.user.PersonalLibraryItemRepository;
import com.example.capstone.arkadia.libris.repository.user.PersonalLibraryRepository;
import com.example.capstone.arkadia.libris.service.stock.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

        PersonalLibraryDto personalLibraryDto = new PersonalLibraryDto();
        personalLibraryDto.setPersonalLibraryId(lib.getId());
        personalLibraryDto.setItems(
                lib.getItems().stream().map(this::toDto).collect(Collectors.toList())
        );
        return personalLibraryDto;
    }

    @Transactional
    public PersonalLIbraryItemDto addItem(Long userId, AddToPersonalLibraryRequestDto request) throws NotFoundException {
        userService.getUser(userId);
        Product p = productService.getProductById(request.getProductId());

        PersonalLIbrary lib = libraryRepo.findByUserId(userId)
                .orElseGet(() -> {
                    PersonalLIbrary x = new PersonalLIbrary();
                    x.setUser(userService.getUser(userId));
                    return libraryRepo.save(x);
                });

        PersonalLibraryItem item = itemRepo
                .findByPersonalLibraryIdAndProductId(lib.getId(), p.getId())
                .orElseGet(() -> {
                    PersonalLibraryItem i = new PersonalLibraryItem();
                    i.setPersonalLibrary(lib);
                    i.setProduct(p);
                    i.setStatus(ReadingStatus.DA_LEGGERE);
                    lib.getItems().add(i);
                    return i;
                });

        libraryRepo.save(lib);
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
