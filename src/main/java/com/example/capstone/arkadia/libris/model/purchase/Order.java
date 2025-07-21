package com.example.capstone.arkadia.libris.model.purchase;

import com.example.capstone.arkadia.libris.enumerated.OrderStatus;
import com.example.capstone.arkadia.libris.model.user.Address;
import com.example.capstone.arkadia.libris.model.user.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate orderDate;
    private double totalAmount;

    @ManyToOne(optional = true)
    @JoinColumn(name = "shipping_address_id", nullable = true, foreignKey = @ForeignKey(name = "fk_order_shipping_address")
    )
    private Address shippingAddress;

    @ManyToOne(optional = true)
    @JoinColumn(name = "billing_address_id", nullable = true, foreignKey = @ForeignKey(name = "fk_order_billing_address"))
    private Address billingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;
}

