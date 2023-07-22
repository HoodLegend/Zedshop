package com.ecommerce.zedshop.models.dto;

import com.ecommerce.zedshop.models.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductDto {
    private Long productId;
    private String product_name;
    private String description;
    private double cost_price;
    private double salePrice;
    private int quantity;
    private Category category;
    private String image;
    private boolean is_activated;
    private boolean is_deleted;
}
