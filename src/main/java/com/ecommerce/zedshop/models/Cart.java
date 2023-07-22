package com.ecommerce.zedshop.models;


import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    @Id
    @SequenceGenerator(
            name = "cart_sequence",
            sequenceName = "cart_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cart_sequence"
    )
    private Long cart_id;

    private Integer totalItems;
    private Double totalPrices;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cart")
    private Set<CartItem> cartItem;

    @Override
    public String toString() {
        return "Cart{" +
                "cart_id=" + cart_id +
                ", totalItems=" + totalItems +
                ", totalPrices=" + totalPrices +
                ", cartItem=" + cartItem +
                '}';
    }

}
