package com.ecommerce.zedshop.services;

import com.ecommerce.zedshop.exceptions.CustomerNotFoundException;
import com.ecommerce.zedshop.models.Product;
import com.ecommerce.zedshop.models.Role;
import com.ecommerce.zedshop.models.User;
import com.ecommerce.zedshop.models.dto.UserDto;
import com.ecommerce.zedshop.repositories.RoleRepository;
import com.ecommerce.zedshop.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class UserService {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;


    public String addUser(User user){
        user.setPassword( bCryptPasswordEncoder.encode(user.getPassword()));
        Role defaultRole = roleRepository.findByName("USER");
        user.setDefaultRole(Collections.singletonList(defaultRole));
        userRepository.save(user);
        return "redirect:/register?success";
    }


    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public void updateResetPasswordToken(String token, String email) throws CustomerNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setResetPasswordToken(token);
            userRepository.save(user);
        } else {
            throw new CustomerNotFoundException("Could not find any customer with the email " + email);
        }
    }

    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public boolean isEmailExists(String email){
        return userRepository.existsByEmail(email);
    }

    public boolean isUsernameExists(String username){
        return userRepository.existsByUsername(username);
    }

    public UserDto getUser(String username){

        UserDto userDto = new UserDto();
        User user = userRepository.findByUsername(username);
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        userDto.setEmail(user.getEmail());

        return userDto;

    }

    public User changePassword(UserDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        return userRepository.save(user);
    }

    public User update(UserDto dto,UserDto userDto) {
        User user = userRepository.findByUsername(dto.getUsername());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        return userRepository.save(user);
    }

}
