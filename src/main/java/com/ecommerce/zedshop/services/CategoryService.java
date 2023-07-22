package com.ecommerce.zedshop.services;

import com.ecommerce.zedshop.models.Category;
import com.ecommerce.zedshop.models.dto.CategoryDto;
import com.ecommerce.zedshop.repositories.CartItemRepository;
import com.ecommerce.zedshop.repositories.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {

    @Autowired
    private final CategoryRepository categoryRepository;

    public Category addCategory(String category_name)
    {
        Category category = new Category();
        category.setCategory_name(category_name);

        return categoryRepository.save(category);
    }


    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    public Category update(Category category) {
        Category categoryUpdate = null;
        try {
            categoryUpdate= categoryRepository.findById(category.getCategoryId()).get();
            categoryUpdate.setCategory_name(category.getCategory_name());
            categoryUpdate.set_activated(category.is_activated());
            categoryUpdate.set_deleted(category.is_deleted());
        }catch (Exception e){
            e.printStackTrace();
        }
        assert categoryUpdate != null;
        return categoryRepository.save(categoryUpdate);
    }


    public List<Category> findAllByActivated() {
        return categoryRepository.findAllByActivated();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id).get();
    }

    public void enabledById(Long id) {
        Category category = categoryRepository.getById(id);
        category.set_activated(true);
        category.set_deleted(false);
        categoryRepository.save(category);
    }

    public void deleteById(Long id) {
        Category category = categoryRepository.getById(id);
        category.set_deleted(true);
        category.set_activated(false);
        categoryRepository.save(category);
    }



    public List<CategoryDto> getCategoryAndProduct() {
        return categoryRepository.getCategoryAndProduct();
    }

}
