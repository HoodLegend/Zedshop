package com.ecommerce.zedshop.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @SequenceGenerator(name = "order_sequence",
    sequenceName = "order_sequence",
    allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "order_sequence")
    private Long orderId;
    private Date orderDate;
    private Date deliveryDate;
    private Double totalPrice;
    private Double shippingFee;
    private String orderStatus;
    private boolean userCancelled = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    private List<OrderDetails> orderDetailList;

    public void setUserCancelled(boolean b) {
    }
}
