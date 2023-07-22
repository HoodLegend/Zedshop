package com.ecommerce.zedshop.repositories;

import com.ecommerce.zedshop.models.Category;
import com.ecommerce.zedshop.models.dto.CategoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

   @Query("select c from Category c where c.is_activated = true and c.is_deleted = false")
   List<Category> findAllByActivated();
   @Query("select new com.ecommerce.zedshop.models.dto.CategoryDto(c.categoryId, c.category_name, count(p.category.categoryId)) from Category c inner join Product p on p.category.categoryId = c.categoryId " +
           " where c.is_activated = true and c.is_deleted = false group by c.categoryId")
   List<CategoryDto> getCategoryAndProduct();

}
