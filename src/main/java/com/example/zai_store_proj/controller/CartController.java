package com.example.zai_store_proj.controller;

import com.example.zai_store_proj.model.Cart;
import com.example.zai_store_proj.model.Product;
import com.example.zai_store_proj.model.User;
import com.example.zai_store_proj.service.CartService;
import com.example.zai_store_proj.service.ProductService;
import com.example.zai_store_proj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId, @RequestParam(name = "quantity", defaultValue = "1") int quantity, Authentication authentication) {
        Product product = productService.getProductById(productId);
        Optional<User> userOptional = userService.findByUsername(authentication.getName());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Cart cart = cartService.getCartById(user.getCart().getId()); // Assuming cart for user is retrieved by some identifier
            cartService.addProductToCart(cart, product, quantity);
        }
        return "redirect:/cart/view";
    }

    @GetMapping("/view")
    public String viewCart(Model model, Authentication authentication) {
        Optional<User> userOptional = userService.findByUsername(authentication.getName());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Cart cart = cartService.getCartById(user.getCart().getId()); // Assuming cart for user is retrieved by some identifier
            model.addAttribute("cart", cart);
        }
        return "view-cart";
    }

    @GetMapping("/remove/{cartItemId}")
    public String removeCartItem(@PathVariable Long cartItemId) {
        cartService.removeCartItem(cartItemId);
        return "redirect:/cart/view";
    }

    @GetMapping("/updateCartItem")
    public String updateCartItem(@RequestParam Long cartItemId, @RequestParam Integer quantity) {
        cartService.updateCartItemQuantity(cartItemId, quantity);
        return "redirect:/cart/view";
    }

    // Other methods for checkout, updating cart items, etc.
}
