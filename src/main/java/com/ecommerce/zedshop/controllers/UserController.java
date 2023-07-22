package com.ecommerce.zedshop.controllers;

import com.ecommerce.zedshop.models.Order;
import com.ecommerce.zedshop.models.User;
import com.ecommerce.zedshop.models.dto.UserDto;
import com.ecommerce.zedshop.repositories.UserRepository;
import com.ecommerce.zedshop.services.OrderService;
import com.ecommerce.zedshop.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private final OrderService orderService;



    @GetMapping("/register")
    public String register(Model model){

        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/change-password")
    public String changePass (@RequestParam("oldPassword") String oldPassword,
                             @RequestParam("newPassword") String newPassword,
                             @RequestParam("repeatNewPassword") String repeatPassword,
                             RedirectAttributes attributes,
                             Model model,
                             Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            UserDto userDto = userService.getUser(principal.getName());
            if (passwordEncoder.matches(oldPassword, userDto.getPassword())
                    && !passwordEncoder.matches(newPassword, oldPassword)
                    && !passwordEncoder.matches(newPassword, userDto.getPassword())
                    && repeatPassword.equals(newPassword) && newPassword.length() >= 5) {
                userDto.setPassword(passwordEncoder.encode(newPassword));
                userService.changePassword(userDto);
                attributes.addFlashAttribute("success", "Your password has been changed successfully!");
            } else {
                model.addAttribute("message", "Your password is wrong");
            }
            return "redirect:/profile";
        }
    }



    @PostMapping("/process-register")
    public String processRegistration(User user, Model model){
        if(userService.isEmailExists(user.getEmail())){
            model.addAttribute("emailExists", true);
            return "register";
        }else if(userService.isUsernameExists(user.getUsername())){
            model.addAttribute("usernameExists", true);
            return "register";
        }

        return userService.addUser(user);
    }

    @GetMapping("/users")
    public String getUsers(Model model){

        List<User> users = userService.getAllUsers();
        model.addAttribute("size", users.size());
        model.addAttribute("users", users);
        return "users";
    }


    @GetMapping("/profile")
    public String userProfile(Model model, Principal principal){
        if (principal == null){
            return "redirect:/login";
        }
            String username = principal.getName();
            UserDto user = userService.getUser(username);
            model.addAttribute("user", user);
            List<Order> orderList = orderService.findAllOrders();
            model.addAttribute("orders",orderList);
            return "profile";

    }

    @GetMapping("/login")
    public String Login_form(){
        return "login";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@Validated @ModelAttribute("user") UserDto userDto,
                                BindingResult result,
                                RedirectAttributes attributes,
                                Model model,
                                Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

            String username = principal.getName();
            UserDto user = userService.getUser(username);

            if (result.hasErrors()) {
                return "profile";
            }

            userService.update(user,userDto);
            UserDto userUpdate = userService.getUser(principal.getName());
            attributes.addFlashAttribute("success", "Update successfully!");
            model.addAttribute("user", userUpdate);
            return "redirect:/profile";
    }


}
