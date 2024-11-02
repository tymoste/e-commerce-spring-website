package com.example.zai_store_proj.service;

import com.example.zai_store_proj.model.Cart;
import com.example.zai_store_proj.model.CartItem;
import com.example.zai_store_proj.model.Product;
import com.example.zai_store_proj.repository.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId).orElse(null);
    }

    @Transactional
    public void addProductToCart(Cart cart, Product product, int quantity) {
        // Sprawdź, czy produkt już istnieje w koszyku
        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Zaktualizuj istniejący produkt
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setTotalPrice(existingItem.getTotalPrice() + (product.getPrice() * quantity));
        } else {
            // Dodaj nowy produkt do koszyka
            CartItem newItem = new CartItem();
            newItem.setCart(cart); // Ustaw odwrotną relację
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setTotalPrice(product.getPrice() * quantity);

            cart.getCartItems().add(newItem);
        }

        cartRepository.save(cart);
    }

    @Transactional
    public void removeCartItem(Long cartItemId) {
        // Pobierz encję CartItem do usunięcia
        CartItem cartItemToRemove = cartRepository.findCartItemById(cartItemId);

        if (cartItemToRemove != null) {
            // Usuń encję CartItem z powiązanego koszyka
            Cart cart = cartItemToRemove.getCart();
            cart.getCartItems().remove(cartItemToRemove);
            cartRepository.save(cart);
        } else {
            // Obsługa sytuacji, gdy encja CartItem nie istnieje
            throw new IllegalArgumentException("CartItem with id " + cartItemId + " not found.");
        }
    }
    @Transactional
    public void updateCartItemQuantity(Long cartItemId, int newQuantity) {
        // Pobierz encję CartItem do aktualizacji
        CartItem cartItemToUpdate = cartRepository.findCartItemById(cartItemId);

        if (cartItemToUpdate != null) {
            // Aktualizuj ilość produktu w koszyku
            cartItemToUpdate.setQuantity(newQuantity);
            cartItemToUpdate.setTotalPrice(cartItemToUpdate.getProduct().getPrice() * newQuantity);

            cartRepository.save(cartItemToUpdate.getCart()); // Zapisz zaktualizowany koszyk
        } else {
            // Obsługa sytuacji, gdy encja CartItem nie istnieje
            throw new IllegalArgumentException("CartItem with id " + cartItemId + " not found.");
        }
    }

    @Transactional
    public void clearCart(Cart cart) {
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    @Transactional
    public Cart getCartWithItemsById(Long id) {
        return cartRepository.findWithItemsById(id);
    }

    // Other methods for managing cart
}
