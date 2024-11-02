package com.example.zai_store_proj.controller;

import com.example.zai_store_proj.model.Category;
import com.example.zai_store_proj.model.Product;
import com.example.zai_store_proj.service.CategoryService;
import com.example.zai_store_proj.service.ProductService;
import com.example.zai_store_proj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listProducts(Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "5") int size,
                               @RequestParam(value = "search", defaultValue = "") String search,
                               @RequestParam(value = "category", required = false) Long categoryId) {
        Page<Product> productPage;
        if (categoryId != null) {
            productPage = productService.getProductsByCategory(categoryId, search, page, size);
        } else {
            productPage = productService.getAllProducts(search, page, size);
        }
        model.addAttribute("productPage", productPage);
        model.addAttribute("search", search);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products";
    }


    @GetMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "addProduct";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             @RequestParam("category") Long categoryId,
                             BindingResult bindingResult) {
        try {
            Category category = categoryService.getCategoryById(categoryId);
            product.setCategory(category);

            if (!imageFile.isEmpty()) {
                product.setImageFile(imageFile);
            }

            if(bindingResult.hasErrors()) {
                return "addProduct";
            }

            productService.saveProduct(product);
            return "redirect:/products";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/products/add";
        }
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "editProduct";
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String editProduct(@Valid @ModelAttribute("product") Product product,
                              BindingResult bindingResult,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              Model model) {
        if (bindingResult.hasErrors()) {
            return "editProduct";
        }
        product.setImageFile(imageFile);
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}

