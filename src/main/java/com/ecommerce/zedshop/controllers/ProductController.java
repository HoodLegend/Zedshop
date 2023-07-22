package com.ecommerce.zedshop.controllers;

import com.ecommerce.zedshop.models.Category;
import com.ecommerce.zedshop.models.Product;
import com.ecommerce.zedshop.models.dto.CategoryDto;
import com.ecommerce.zedshop.models.dto.ProductDto;
import com.ecommerce.zedshop.services.CategoryService;
import com.ecommerce.zedshop.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {
    @Autowired
    private final ProductService productService;

    @Autowired
    private final CategoryService categoryService;

    @GetMapping("/upload-products")
    public String addProducts(Model model, Principal principal)
    {
        if(principal == null){
            return "redirect:/login";
        }
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("category", categories);
        return "add-products";
    }

    // Handles Save request
    @PostMapping("/save-product")
    public String saveProduct (@RequestParam("file") MultipartFile file,
                               @RequestParam(value = "product_name") String product_name,
                               @RequestParam("cost_price") Double cost_price,
                               @RequestParam("description") String description,
                               @RequestParam("quantity") Integer quantity,
                               @RequestParam(value = "category") Category categories,
                               Model model)
    {

        if(product_name == null || product_name.isEmpty())
        {
            model.addAttribute("error", "Product name is required");
            List<Category> category = categoryService.getAllCategories();
            model.addAttribute("category", category);
            return "add-products";
        }
        productService.saveProductToDB(file, product_name, description, cost_price, quantity, categories);
        return "redirect:/list-products";
    }


    @GetMapping("/list-products")
    public String showListOfProducts(Model model)
    {
        List<Product> products = productService.getAllProduct();
        model.addAttribute("products", products);
        return "dashboard";
    }


    // Handle delete request
    @PostMapping("/delete/{productId}")
    public String deleteProduct(@PathVariable("productId") String productId)
    {
        Long id = Long.parseLong(productId);
        productService.deleteProduct(id);
        return "redirect:/list-products";
    }

   // Handle edit request
    @PostMapping("/edit/{productId}")
    public String editProduct(@PathVariable("productId") Long productId,
                              @ModelAttribute("product") Product product)
    {
        productService.updateProduct(productId, product.getProduct_name(),
                product.getDescription(),
                product.getCost_price(),
                product.getQuantity());

        return "redirect:/list-products";
    }

    @GetMapping("/product/{productId}")
    public String showProductDetails(@PathVariable("productId") Long productId,
                                     Model model){
        Product product = productService.getProductById(productId);
        model.addAttribute("product", product);
        return "product-detail";
    }

    @GetMapping("/products-in-category/{id}")
    public String getProductsInCategory(@PathVariable("id") Long categoryId ,Model model)
    {
        Category category = categoryService.findById(categoryId);
        List<CategoryDto> categories = categoryService.getCategoryAndProduct();
        List<Product> products = productService.getProductsInCategory(categoryId);
        model.addAttribute("category",category);
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        return "products-in-category";
    }

    @GetMapping("/search-products/{pageNo}")
    public String searchProduct(@PathVariable("pageNo") int pageNo,
                                @RequestParam(value = "keyword") String keyword,
                                Model model
    ) {
        List<ProductDto> products = productService.searchProducts( keyword);
        model.addAttribute("title", "Result Search Products");
        model.addAttribute("products", products);
        model.addAttribute("currentPage", pageNo);

        return "redirect:/dashboard";

    }

    @GetMapping("/search/{pageNo}")
    public String customerSearch(@PathVariable("pageNo") Integer pageNo,
                                 @RequestParam(value = "keyword") String keyword,
                                 Model model
    ) {
        List<ProductDto> products = productService.searchProducts( keyword);
        model.addAttribute("title", "Result Search Products");
        model.addAttribute("products", products);
        model.addAttribute("currentPage", pageNo);

        return "search-results";

    }

}
