package com.ecommerce.zedshop.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetails {
    @Id
    @SequenceGenerator(name = "order_detail_sequence",
    sequenceName = "order_detail_sequence" , allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "order_detail_sequence")
    @Column(name = "order_detail_id")
    private Long orderDetailId;

    private Integer quantity;
    private Double totalPrice;
    private Double unitPrice;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", referencedColumnName = "orderId")
    private Order order;

    @OneToOne(cascade =CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;
}

