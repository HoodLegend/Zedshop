package com.ecommerce.zedshop.controllers;

import com.ecommerce.zedshop.models.Cart;
import com.ecommerce.zedshop.models.CartItem;
import com.ecommerce.zedshop.models.Product;
import com.ecommerce.zedshop.models.User;
import com.ecommerce.zedshop.services.CartService;
import com.ecommerce.zedshop.services.ProductService;
import com.ecommerce.zedshop.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;
import java.util.Set;

@Controller
@AllArgsConstructor
public class CartController {

    @Autowired
    private final CartService cartService;

    @Autowired
    private final ProductService productService;

    @Autowired
    private final UserService userService;


    @GetMapping("/cart")
    public String cart(Model model, Principal principal, HttpSession session)
    {
        if(principal == null){
            return "redirect:/login";
        }

        String username = principal.getName();
        User user = userService.findByUsername(username);
        Cart cart = user.getCart();

        // check to see if the cart is empty and that the cart is not null.
        if(cart == null || cart.getCartItem().isEmpty()){
            model.addAttribute("check", "No Items in the Cart!<br>Go to catalog to shop items");
            session.setAttribute("subTotal", 0);
            model.addAttribute("totalItems", 0);
        }else {
            session.setAttribute("totalItems", Objects.requireNonNull(cart).getTotalItems());
            model.addAttribute("subTotal", cart.getTotalPrices());
            model.addAttribute("cart", cart);

        }

        return "cart";
    }


    @PostMapping("/add-to-cart")
    public String addItemToCart(
            @RequestParam("id") Long productId,
            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
            Principal principal,
            HttpServletRequest request)
    {

        if(principal == null){
            return "redirect:/login";
        }
        Product product = productService.getProductById(productId);
        String username = principal.getName();
        User user = userService.findByUsername(username);


        Cart cart = cartService.addItemToCart(product, quantity, user);
        return "redirect:/cart";
    }

    @RequestMapping(value = "/update-cart", method = RequestMethod.POST, params = "action=update")
    public String updateCart(@RequestParam("quantity") Integer quantity,
                             @RequestParam("id") Long productId,
                             Model model,
                             Principal principal
    ){

        if(principal == null){
            return "redirect:/login";
        }else{
            String username = principal.getName();
            User user = userService.findByUsername(username);
            Product product = productService.getProductById(productId);
            Cart cart = cartService.updateItemInCart(product, quantity, user);
            model.addAttribute("Cart", cart);
            return "redirect:/cart";
        }
    }

    @RequestMapping(value = "/update-cart", method = RequestMethod.POST, params = "action=delete")
    public String deleteItemFromCart(@RequestParam("id") Long productId,
                                     Model model,
                                     Principal principal)
    {
        if(principal == null){
            return "redirect:/login";
        }else{
            String username = principal.getName();
            User user = userService.findByUsername(username);
            Product product = productService.getProductById(productId);
            Cart cart = cartService.deleteItemFromCart(product, user);
            model.addAttribute("Cart", cart);
            return "redirect:/cart";
        }

    }
}
