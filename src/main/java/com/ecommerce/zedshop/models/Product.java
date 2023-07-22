package com.ecommerce.zedshop.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @SequenceGenerator(
            name="product_sequence",
            sequenceName = "product_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_sequence"
    )
    private Long productId;

    private String product_name;

    private Double cost_price;

    private Double Sale_price;

    @CreationTimestamp
    private Date created;

    private String description;

    private Integer quantity;


    @Column(columnDefinition = "VARBINARY")
    private String image;

    @ManyToOne(
            fetch = FetchType.EAGER, cascade = CascadeType.ALL
    )
    @JoinColumn(name = "categoryId", referencedColumnName = "categoryId")
    private Category category;

    private boolean is_deleted;
    private boolean is_activated;

    @Override
    public String toString() {
        return super.toString();
    }
}
