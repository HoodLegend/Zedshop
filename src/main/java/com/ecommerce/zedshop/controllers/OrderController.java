package com.ecommerce.zedshop.controllers;

import com.ecommerce.zedshop.models.Cart;
import com.ecommerce.zedshop.models.Order;
import com.ecommerce.zedshop.models.Product;
import com.ecommerce.zedshop.models.User;
import com.ecommerce.zedshop.services.OrderService;
import com.ecommerce.zedshop.services.ProductService;
import com.ecommerce.zedshop.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
public class OrderController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final OrderService orderService;

    @Autowired
    private final ProductService productService;

    @GetMapping("/check-out")
    public String checkout(Model model, Principal principal){
        if(principal == null){
            return "redirect:/login";
        }

        String username = principal.getName();
        User user = userService.findByUsername(username);
        if (user.getEmail().trim().isEmpty()
                || user.getUsername().trim().isEmpty()){

            model.addAttribute("user", user);
            model.addAttribute("error", "You must fill the information after checkout!");
            return "account";
        } else {
            model.addAttribute("user", user);
            Cart cart = user.getCart();
            if(cart == null || cart.getCartItem().isEmpty()){
                model.addAttribute("check", "Please Add some<br>things to your cart!");
                model.addAttribute("totalItems", 0);
                model.addAttribute("subTotal", 0);
            }else{
                model.addAttribute("totalItems", cart.getTotalItems());
                model.addAttribute("subTotal", cart.getTotalPrices());
            }
            model.addAttribute("cart", cart);
        }

        return "checkout";
    }

    @RequestMapping(value="/buy/{id}" , method = {RequestMethod.PUT , RequestMethod.GET})
    public String buy(@PathVariable("id")Long id,
                      Principal principal,
                      Model model)
    {
        if(principal == null){
            return "redirect:/login";
        }

        String username= principal.getName();
        User user = userService.findByUsername(username);

        Product product = productService.getProductById(id);
        model.addAttribute("user", user);
        model.addAttribute("product", product);
        return "checkout-2";
    }

    @RequestMapping(value="/save-direct-order/{id}")
    public String saveDirectOrder(@PathVariable("id")Long id, Principal principal){
        if (principal==null){
            return "redirect:/login";
        }
        String username = principal.getName();
        User user=userService.findByUsername(username);
        Product product=productService.getProductById(id);
        orderService.saveDirectOrder(user,product);

        return "redirect:/order";
    }

    @GetMapping("/order")
    public String order(Principal principal,Model model){
        if (principal==null){
            return "redirect:/login";
        }
        String username = principal.getName();
        User user = userService.findByUsername(username);
        List<Order> orderList = user.getOrders();
        model.addAttribute("orders", orderList);

        return "order";
    }

    @PostMapping("/save-order")
    public String saveOrder(Principal principal,
                            @RequestParam("phoneNumber")String phoneNumber,
                            @RequestParam("currency")String currency,
                            @RequestParam("country")String country){
        if (principal==null){
            return "redirect:/login";
        }
        String username = principal.getName();
        User user=userService.findByUsername(username);
        Cart cart=user.getCart();

        Double totalPrice = cart.getTotalPrices();
        orderService.saveOrder(cart, phoneNumber, currency, country);

        return "redirect:/order";
    }

    @RequestMapping(value = "/accept-order/{id}", method = {RequestMethod.PUT , RequestMethod.GET})
    public String acceptOrder(@PathVariable("id")Long id, RedirectAttributes attributes){
        orderService.acceptOrder(id);
        return "redirect:/admin-orders";
    }

    @GetMapping("/admin-orders")
    public String findAllOrders(Principal principal,Model model){
        if (principal==null){
            return "redirect:/login";
        }

        String username = principal.getName();
        User user = userService.findByUsername(username);
        List<Order> orderList = orderService.findAllOrders();
        model.addAttribute("orders",orderList);
        return "admin-order";
    }

    @RequestMapping(value = "/cancel-order/{id}", method = {RequestMethod.PUT , RequestMethod.GET})
    public String cancelOrder(@PathVariable("id")Long id, RedirectAttributes attributes){
        orderService.cancelOrder(id);
        return "redirect:/admin-orders";
    }

    @RequestMapping(value = "/user-cancel-order/{id}", method = {RequestMethod.PUT , RequestMethod.GET})
    public String UsercancelOrder(@PathVariable("id")Long id, RedirectAttributes attributes){
        orderService.cancelOrder(id);
        return "redirect:/order";
    }
}
