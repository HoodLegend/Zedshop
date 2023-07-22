package com.ecommerce.zedshop.services;

import com.ecommerce.zedshop.models.Cart;
import com.ecommerce.zedshop.models.CartItem;
import com.ecommerce.zedshop.models.Product;
import com.ecommerce.zedshop.models.User;
import com.ecommerce.zedshop.repositories.CartItemRepository;
import com.ecommerce.zedshop.repositories.CartRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
public class CartService {

    @Autowired
    private final CartRepository cartRepository;

    @Autowired
    private final CartItemRepository cartItemRepository;


    public Cart addItemToCart(Product product, Integer quantity, User user) {
        Cart cart = user.getCart();

        if (cart == null) {
            cart = new Cart();
        }
        Set<CartItem> cartItems = cart.getCartItem();
        CartItem cartItem = findCartItem(cartItems, product.getProductId());
        if (cartItems == null) {
            cartItems = new HashSet<>();
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setTotalPrice(quantity * product.getCost_price());
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart);
            cartItems.add(cartItem);
            cartItemRepository.save(cartItem);
        } else {
            if (cartItem == null) {
                // Check if the cart already contains the same product
                boolean exists = false;
                for (CartItem item : cartItems) {
                    if (Objects.equals(item.getProduct().getProductId(), product.getProductId())) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    cartItem = new CartItem();
                    cartItem.setProduct(product);
                    cartItem.setTotalPrice(quantity * product.getCost_price());
                    cartItem.setQuantity(quantity);
                    cartItem.setCart(cart);
                    cartItems.add(cartItem);
                    cartItemRepository.save(cartItem);
                }
            } else {
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItem.setTotalPrice(cartItem.getTotalPrice() + ( quantity * product.getCost_price()));
                cartItemRepository.save(cartItem);
            }
        }
        cart.setCartItem(cartItems);

        Integer totalItems = totalItems(cart.getCartItem());
        Double totalPrice = totalPrice(cart.getCartItem());

        cart.setTotalPrices(totalPrice);
        cart.setTotalItems(totalItems);
        cart.setUser(user);

        return cartRepository.save(cart);
    }



    private CartItem findCartItem(Set<CartItem> cartItems, Long productId) {
        if (cartItems == null) {
            return null;
        }
        CartItem cartItem = null;

        for (CartItem item : cartItems) {
            if (Objects.equals(item.getProduct().getProductId(), productId)) {
                cartItem = item;
            }
        }
        return cartItem;
    }

    private int totalItems(Set<CartItem> cartItems){
        Integer totalItems = 0;
        for(CartItem item : cartItems){
            totalItems += item.getQuantity();
        }
        return totalItems;

    }

    private double totalPrice(Set<CartItem> cartItems){
        Double totalPrice = 0.0;

        for(CartItem item : cartItems){
            totalPrice += item.getTotalPrice();
        }

        return totalPrice;
    }


    public Cart updateItemInCart(Product product, Integer quantity, User user) {
        Cart cart = user.getCart();

        Set<CartItem> cartItems = cart.getCartItem();

        CartItem item = findCartItem(cartItems, product.getProductId());

        item.setQuantity(quantity);
        item.setTotalPrice(quantity * product.getCost_price());

        cartItemRepository.save(item);

        Integer totalItems = totalItems(cartItems);
        Double totalPrice = totalPrice(cartItems);

        cart.setTotalItems(totalItems);
        cart.setTotalPrices(totalPrice);

        return cartRepository.save(cart);
    }


    public Cart deleteItemFromCart(Product product, User user) {
        Cart cart = user.getCart();

        Set<CartItem> cartItems = cart.getCartItem();

        CartItem item = findCartItem(cartItems, product.getProductId());

        cartItems.remove(item);

        cartItemRepository.delete(item);

        double totalPrice = totalPrice(cartItems);
        int totalItems = totalItems(cartItems);

        cart.setCartItem(cartItems);
        cart.setTotalItems(totalItems);
        cart.setTotalPrices(totalPrice);

        return cartRepository.save(cart);
    }




}
