package com.example.zai_store_proj.controller;

import com.example.zai_store_proj.model.*;
import com.example.zai_store_proj.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String checkout(Model model, Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Cart cart = cartService.getCartWithItemsById(user.getCart().getId());

            Order order = new Order();
            order.setUser(user);
            order.setTotalAmount(cart.getTotalAmount());
            order.setAddress(user.getAddress());
            order.setPhoneNumber(user.getPhoneNumber()); // Ustawienie domyślnego numeru telefonu

            model.addAttribute("order", order);
            model.addAttribute("cart", cart);
            return "checkout";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/process")
    public String processCheckout(@ModelAttribute("order") Order order,
                                  @RequestParam("address") String address,
                                  @RequestParam("phoneNumber") String phoneNumber,
                                  Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Cart cart = cartService.getCartWithItemsById(user.getCart().getId());

            order.setUser(user);
            order.setTotalAmount(cart.getTotalAmount());
            order.setOrderItems(cart.getCartItems().stream().map(cartItem -> {
                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getTotalPrice());
                return orderItem;
            }).collect(Collectors.toList()));

            order.setOrderDate(LocalDateTime.now());
            order.setOrderStatus(OrderStatus.PENDING);
            order.setAddress(address); // Ustawienie adresu z formularza
            order.setPhoneNumber(phoneNumber); // Ustawienie numeru telefonu z formularza

            orderService.saveOrder(order);
            cartService.clearCart(cart);

            return "redirect:/products";
        } else {
            return "redirect:/login";
        }
    }
}
