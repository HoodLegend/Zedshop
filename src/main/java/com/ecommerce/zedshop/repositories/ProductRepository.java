package com.ecommerce.zedshop.repositories;

import com.ecommerce.zedshop.models.Category;
import com.ecommerce.zedshop.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "select p from Product p inner join Category c on c.categoryId = p.category.categoryId where c.categoryId = ?1 and p.is_deleted = false and p.is_activated = true")
    List<Product> getProductsInCategory(Long categoryId);

    @Query("select p from Product p where p.product_name like %?1% or p.description like %?1%")
    List<Product> searchProducts(String keyword);

}
