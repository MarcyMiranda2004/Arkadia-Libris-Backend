package com.example.capstone.arkadia.libris.service.user;

import com.example.capstone.arkadia.libris.dto.response.user.AddressDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.model.user.Address;
import com.example.capstone.arkadia.libris.model.user.User;
import com.example.capstone.arkadia.libris.repository.purchase.OrderRepository;
import com.example.capstone.arkadia.libris.repository.user.AddressRepository;
import com.example.capstone.arkadia.libris.service.notification.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class AddressService {

    @Autowired private AddressRepository addressRepository;

    @Autowired private UserService userService;

    @Autowired private EmailService emailService;

    @Autowired private OrderRepository orderRepository;

    public AddressDto saveAddress(Long userId, AddressDto addressDto) throws NotFoundException {
        User u = userService.getUser(userId);

        Address a = new Address();
        a.setUser(u);
        a.setName(addressDto.getName());
        a.setStreet(addressDto.getStreet());
        a.setCity(addressDto.getCity());
        a.setProvince(addressDto.getProvince());
        a.setCountry(addressDto.getCountry());
        a.setPostalCode(addressDto.getPostalCode());

        Address saved = addressRepository.save(a);
        emailService.sendEmailAddressaddedNotice(u.getEmail());

        addressDto.setId(saved.getId());
        return addressDto;
    }

    public AddressDto updateAddress(Long userId, Long addressId, AddressDto addressDto)
            throws NotFoundException, AccessDeniedException {
        Address a = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Indirizzo non trovato"));
        if (!a.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Non puoi modificare questo indirizzo");
        }
        a.setName(addressDto.getName());
        a.setStreet(addressDto.getStreet());
        a.setCity(addressDto.getCity());
        a.setProvince(addressDto.getProvince());
        a.setCountry(addressDto.getCountry());
        a.setPostalCode(addressDto.getPostalCode());

        Address saved = addressRepository.save(a);
        addressDto.setId(saved.getId());
        return addressDto;
    }

    @Transactional
    public void deleteAddress(Long userId, Long addressId)
            throws NotFoundException, AccessDeniedException {
        Address a = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Indirizzo non trovato"));
        if (!a.getUser().getId().equals(userId))
            throw new AccessDeniedException("Non puoi eliminare questo indirizzo");

        orderRepository.nullifyShippingAddress(addressId);
        orderRepository.nullifyBillingAddress(addressId);

        addressRepository.delete(a);
    }

    public List<AddressDto> listAddresses(Long userId) throws NotFoundException {
        User u = userService.getUser(userId);
        return u.getAddresses().stream()
                .map(a -> {
                    AddressDto addressDto = new AddressDto();
                    addressDto.setId(a.getId());
                    addressDto.setName(a.getName());
                    addressDto.setStreet(a.getStreet());
                    addressDto.setCity(a.getCity());
                    addressDto.setProvince(a.getProvince());
                    addressDto.setCountry(a.getCountry());
                    addressDto.setPostalCode(a.getPostalCode());
                    return addressDto;
                })
                .toList();
    }
}

