package com.example.capstone.arkadia.libris.repository;

import com.example.capstone.arkadia.libris.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Addressrepository extends JpaRepository<Address, Long> {}
