package com.example.orderservice.entity;

import com.kloia.eventapis.spring.model.JpaEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@javax.persistence.Entity(name = "ORDERS")
public class Order extends JpaEntity {

    @Enumerated(EnumType.STRING)
    private OrderState state;

    private long price; // each objects' price (birim fiyatı)

    private String stockId;

    private int reservedStockVersion;

    private int orderAmount; // kaç ürün alındıgı

    private String paymentAddress;

    private float amount; // toplam price --- PaymentInformation(amount)

    private String cardInformation;

    private String paymentId;

    private String address;

    private String description;
}
