package com.example.zai_store_proj.service;

import com.example.zai_store_proj.model.Cart;
import com.example.zai_store_proj.model.User;
import com.example.zai_store_proj.repository.CartRepository;
import com.example.zai_store_proj.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CartRepository cartRepository;

    public Optional<User> findByUsername(String email) {
        return userRepository.findByEmail(email);
    }

    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles("USER");
        Cart cart = new Cart();
        userRepository.save(user);
        cart.setUser(user);
        cartRepository.save(cart);
    }
}
