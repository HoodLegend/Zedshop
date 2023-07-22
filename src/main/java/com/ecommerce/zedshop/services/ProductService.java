package com.ecommerce.zedshop.services;

import com.ecommerce.zedshop.models.Category;
import com.ecommerce.zedshop.models.Product;
import com.ecommerce.zedshop.models.dto.ProductDto;
import com.ecommerce.zedshop.repositories.CartItemRepository;
import com.ecommerce.zedshop.repositories.CategoryRepository;
import com.ecommerce.zedshop.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@Service
@AllArgsConstructor
public class ProductService {

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final CartItemRepository cartItemRepository;

    @Autowired
    private final CategoryRepository categoryRepository;


    public void saveProductToDB(MultipartFile file, String product_name, String description
            , Double cost_price, Integer quantity, Category categories)
    {

        Product product = new Product();
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if(fileName.contains(".."))
        {
            System.out.println("not a valid file");
        }
        try {
            product.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        product.setDescription(description);
        product.setProduct_name(product_name);
        product.setCost_price(cost_price);
        product.setQuantity(quantity);
        product.setCategory(categories);
        product.set_activated(true);
        product.set_deleted(false);

        productRepository.save(product);
    }


    public List<Product> getAllProduct()
    {
        return productRepository.findAll();
    }

    @Transactional
    public void updateProduct(Long productId, String product_name,
                              String description,
                              Double cost_price, Integer quantity)
    {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("product with id "
                        + productId + " does not exist"));
        // check if the product is not null and exists
        if(product_name != null && !Objects.equals(product.getProduct_name(), product_name)) {
            product.setProduct_name(product_name);
        }

        if(description != null && !Objects.equals(product.getDescription(), description)){
            product.setDescription(description);
        }

        if(cost_price != null  && cost_price > 0.0 && !Objects.equals(product.getCost_price(), cost_price)){
            product.setCost_price(cost_price);
        }

        if(quantity != null  && quantity > 0.0 && !Objects.equals(product.getQuantity(), quantity)){
            product.setQuantity(quantity);
        }


        productRepository.save(product);
    }

    public void deleteProduct(Long productId) {

        // checks to see if the product with the id exists
        // if it does it is deleted if not an exception is thrown
        boolean exists = productRepository.existsById(productId);
        if(!exists){
            throw new IllegalStateException("Product does not exist!!");
        }

//        boolean itemInCart = cartItemRepository.existsById(productId);
//        if(!itemInCart){
//            throw new IllegalStateException("There is no such item in the cart!");
//        }
//        cartItemRepository.deleteById(productId);


        productRepository.deleteById(productId);
    }

    public List<ProductDto> searchProducts(String keyword) {
        return transferData(productRepository.searchProducts(keyword));
    }

    private List<ProductDto> transferData(List<Product> products) {
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = new ProductDto();
            productDto.setProductId(product.getProductId());
            productDto.setProduct_name(product.getProduct_name());
            productDto.setQuantity(product.getQuantity());
            productDto.setCost_price(product.getCost_price());
//            productDto.setSalePrice(product.getSale_price());
            productDto.setDescription(product.getDescription());
            productDto.setImage(product.getImage());
            productDto.setCategory(product.getCategory());
            productDto.set_activated(product.is_activated());
            productDto.set_deleted(product.is_deleted());
            productDtos.add(productDto);
        }
        return productDtos;
    }


    public Product getProductById(Long productId){
        boolean exists = productRepository.existsById(productId);
        if(!exists){
            throw new IllegalStateException("product with this id does exist!!");
        }
       return productRepository.findById(productId).get();
    }


    public List<Product> getProductsInCategory(Long categoryId) {
        return productRepository.getProductsInCategory(categoryId);
    }
}
