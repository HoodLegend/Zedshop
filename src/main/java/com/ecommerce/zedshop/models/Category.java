package com.ecommerce.zedshop.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = "category_name"))
public class Category {
    @Id
    @SequenceGenerator(
            name = "category_sequence",
            sequenceName = "category_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "category_sequence"
    )
    private Long categoryId;

    private String category_name;

    @OneToMany(
            mappedBy = "category",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private List<Product> product;

    private boolean is_activated = true;
    private boolean is_deleted = false;

    public Category(String category_name){
        this.category_name = category_name;
        this.is_activated = true;
        this.is_deleted = false;
    }

}
