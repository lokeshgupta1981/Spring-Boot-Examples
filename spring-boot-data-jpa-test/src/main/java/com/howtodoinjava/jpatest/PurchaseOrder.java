package com.howtodoinjava.jpatest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private String customerEmail;

    protected PurchaseOrder() { }

    public PurchaseOrder(String orderNumber, String customerEmail) {
        this.orderNumber = orderNumber;
        this.customerEmail = customerEmail;
    }

    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public String getCustomerEmail() { return customerEmail; }
}
