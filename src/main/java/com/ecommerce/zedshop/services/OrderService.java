package com.ecommerce.zedshop.services;

import com.ecommerce.zedshop.models.*;
import com.ecommerce.zedshop.repositories.CartItemRepository;
import com.ecommerce.zedshop.repositories.CartRepository;
import com.ecommerce.zedshop.repositories.OrderDetailsRepository;
import com.ecommerce.zedshop.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.ecommerce.zedshop.services.AirtelPay.pay;
import static com.ecommerce.zedshop.services.AirtelPay.token;

@Service
@AllArgsConstructor
public class OrderService {

    @Autowired
    private final CartRepository cartRepository;

    private final OrderDetailsRepository orderDetailsRepository;
    private final OrderRepository orderRepository;

    @Autowired
    private final CartItemRepository cartItemRepository;

    @Autowired
    private final ProductService productService;

//    public void saveOrder(Cart cart){
//
//        if (cart == null) {
//            throw new IllegalArgumentException("Cart cannot be null.");
//        }
//
//        // checks if cart is empty and does not allow user to check out.
//        if (cart.getCartItem().isEmpty()) {
//            throw new IllegalStateException("Cannot checkout with an empty cart.");
//        }
//
//        Order order = new Order();
//        order.setOrderStatus("PENDING");
//        order.setOrderDate(new Date());
//        order.setUser(cart.getUser());
//        order.setTotalPrice(cart.getTotalPrices());
//        List<OrderDetails> orderDetailList = new ArrayList<>();
//
//
//        for (CartItem item : cart.getCartItem()) {
//            OrderDetails orderDetail = new OrderDetails();
//            orderDetail.setOrder(order);
//            orderDetail.setQuantity(item.getQuantity());
//            orderDetail.setProduct(item.getProduct());
//            orderDetail.setUnitPrice(item.getProduct().getCost_price());
//
//            orderDetailsRepository.save(orderDetail);
//            orderDetailList.add(orderDetail);
//
//            cartItemRepository.delete(item);
//        }
//        order.setOrderDetailList(orderDetailList);
//        cart.setCartItem(new HashSet<>());
//        cart.setTotalItems(0);
//        cart.setTotalPrices(0.00);
//        cartRepository.save(cart);
//
//        orderRepository.save(order);
//    }

    public void saveOrder(Cart cart, String phoneNumber, String currency, String country) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null.");
        }

        // Checks if cart is empty and does not allow the user to check out.
        if (cart.getCartItem().isEmpty()) {
            throw new IllegalStateException("Cannot checkout with an empty cart.");
        }

        try {
            // Step 1: Get the access token
            String accessToken = token().getString("access_token");

            Double totalPrice = cart.getTotalPrices();

            // Step 2: Make the payment
            JSONObject paymentResult = pay(phoneNumber, totalPrice ,currency, country, accessToken);

            // Step 3: Process the payment result
            int statusCode = paymentResult.getInt("status");
            if (statusCode == 200) {
                JSONObject jsonData = paymentResult.getJSONObject("jsondata");
                // Payment successful, save the order
                Order order = new Order();
                order.setOrderStatus("PENDING");
                order.setOrderDate(new Date());
                order.setUser(cart.getUser());
                order.setTotalPrice(cart.getTotalPrices());
                List<OrderDetails> orderDetailList = new ArrayList<>();

                for (CartItem item : cart.getCartItem()) {
                    OrderDetails orderDetail = new OrderDetails();
                    orderDetail.setOrder(order);
                    orderDetail.setQuantity(item.getQuantity());
                    orderDetail.setProduct(item.getProduct());
                    orderDetail.setUnitPrice(item.getProduct().getCost_price());

                    orderDetailsRepository.save(orderDetail);
                    orderDetailList.add(orderDetail);

                    cartItemRepository.delete(item);
                }
                order.setOrderDetailList(orderDetailList);
                cart.setCartItem(new HashSet<>());
                cart.setTotalItems(0);
                cart.setTotalPrices(0.00);
                cartRepository.save(cart);

                orderRepository.save(order);
            } else {
                // Payment failed, handle accordingly
                // ...
                throw new IllegalStateException("Failed to make payment for this item.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle IO exception
            // ...
        }
    }


    public void acceptOrder(Long id ){
        Order order=orderRepository.getReferenceById(id);
        order.setDeliveryDate(new Date());
        order.setOrderStatus("Shipping");
        for (OrderDetails detail : order.getOrderDetailList()){
            Product product = productService.getProductById(detail.getProduct().getProductId());
            product.setQuantity(product.getQuantity()-detail.getQuantity());
        }
        orderRepository.save(order);
    }

    public void cancelOrder(Long id){
        // check if the order status is equal to Shipping
        Order order = orderRepository.getReferenceById(id);
        if (order.getOrderStatus().equals("Shipping")) {
            // set the order status cancelled and add the products back to inventory
            order.setOrderStatus("Cancelled");
            for (OrderDetails detail : order.getOrderDetailList()){
                Product product = productService.getProductById(detail.getProduct().getProductId());
                product.setQuantity(product.getQuantity() + detail.getQuantity());
            }
            // set a flag indicating the order was cancelled by the user
            order.setUserCancelled(true);
            orderRepository.save(order);
        } else {
            // Handle error or display message indicating order cannot be cancelled
            throw new IllegalArgumentException("Order cannot be cancelled");
        }
    }


    public List<Order> findAllOrders(){
        return orderRepository.findAll();
    }


    public void saveDirectOrder(User user, Product product) {

            Order order = new Order();
            order.setOrderStatus("PENDING");
            order.setOrderDate(new Date());
            order.setUser(user);
            order.setTotalPrice(product.getCost_price());

            OrderDetails orderDetail = new OrderDetails();
            orderDetail.setOrder(order);
            orderDetail.setQuantity(1);
            orderDetail.setProduct(product);
            orderDetail.setUnitPrice(product.getCost_price());
            orderDetailsRepository.save(orderDetail);
            orderRepository.save(order);
        }

}
