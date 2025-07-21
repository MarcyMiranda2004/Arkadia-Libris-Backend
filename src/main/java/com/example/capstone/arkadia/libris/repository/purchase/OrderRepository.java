package com.example.capstone.arkadia.libris.repository.purchase;

import com.example.capstone.arkadia.libris.model.purchase.Order;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.shippingAddress = NULL, o.billingAddress  = NULL WHERE o.user.id = :userId")
    void nullifyAddressesForUser(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.shippingAddress = NULL WHERE o.shippingAddress.id = :addressId")
    void nullifyShippingAddress(@Param("addressId") Long addressId);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.billingAddress = NULL WHERE o.billingAddress.id = :addressId")
    void nullifyBillingAddress(@Param("addressId") Long addressId);

    @Modifying @Query("delete from Order o where o.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
