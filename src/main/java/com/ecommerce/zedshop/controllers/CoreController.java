package com.ecommerce.zedshop.controllers;

import com.ecommerce.zedshop.models.Cart;
import com.ecommerce.zedshop.models.Order;
import com.ecommerce.zedshop.models.Product;
import com.ecommerce.zedshop.models.User;
import com.ecommerce.zedshop.models.dto.CategoryDto;
import com.ecommerce.zedshop.repositories.UserRepository;
import com.ecommerce.zedshop.services.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class CoreController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final ProductService productService;

    @Autowired
    private final CategoryService categoryService;


    @Autowired
    private final OrderService orderService;



    @GetMapping("/")
    public String home(Model model, Principal principal, HttpSession session){
        List<Product> products = productService.getAllProduct();
        List<CategoryDto> categories = categoryService.getCategoryAndProduct();

        if(principal != null){
            String username = principal.getName();
            User user = userService.findByUsername(username);
            Cart cart = user.getCart();

            if(cart == null || cart.getCartItem().isEmpty()){
                model.addAttribute("check", "No Items in the Cart!<br>Go to catalog to shop items");
                session.setAttribute("subTotal", 0);
                model.addAttribute("totalItems", 0);
            }else {
                session.setAttribute("totalItems", Objects.requireNonNull(cart).getTotalItems());
                model.addAttribute("subTotal", cart.getTotalPrices());
                model.addAttribute("cart", cart);
            }

        }else{
            session.removeAttribute("username");
        }

        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        return "index";
    }


    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String viewAll(Model model){

        List<Product> products = productService.getAllProduct();
        model.addAttribute("products", products);
        return "dashboard";
    }


}
