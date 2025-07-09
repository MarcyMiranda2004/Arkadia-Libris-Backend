package com.example.capstone.arkadia.libris.repository.user;

import com.example.capstone.arkadia.libris.model.user.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {}
