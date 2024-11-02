package com.example.zai_store_proj.controller;

import com.example.zai_store_proj.model.User;
import com.example.zai_store_proj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "register"; // Powrót do formularza rejestracji z błędami
        }

        userService.saveUser(user);
        return "redirect:/login"; // Przekierowanie po poprawnej rejestracji
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('USER') or hasAnyAuthority('ADMIN')")
    public String userProfile(Model model) {
        Optional<User> user = userService.findByUsername(getLoggedInUserDetails().getUsername());
        user.ifPresent(user1 -> model.addAttribute("user", user1));
        return "profile";
    }

    public UserDetails getLoggedInUserDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof UserDetails){
            return (UserDetails) authentication.getPrincipal();
        }
        return null;
    }

}

