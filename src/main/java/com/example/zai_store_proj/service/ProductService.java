package com.example.zai_store_proj.service;

import com.example.zai_store_proj.model.Product;
import com.example.zai_store_proj.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class ProductService {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @Autowired
    private ProductRepository productRepository;

    public Page<Product> getAllProducts(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (search == null || search.isEmpty()) {
            return productRepository.findAll(pageable);
        }
        return productRepository.findAll((root, query, cb) -> cb.like(root.get("name"), "%" + search + "%"), pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Page<Product> getProductsByCategory(Long categoryId, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll((root, query, cb) -> {
            if (search == null || search.isEmpty()) {
                return cb.equal(root.get("category").get("id"), categoryId);
            } else {
                return cb.and(
                        cb.equal(root.get("category").get("id"), categoryId),
                        cb.like(root.get("name"), "%" + search + "%")
                );
            }
        }, pageable);
    }


    public void saveProduct(Product product) {
        if (product.getImageFile() != null && !product.getImageFile().isEmpty()) {
            String imageUrl = saveImage(product.getImageFile());
            product.setImageUrl(imageUrl);
        }
        productRepository.save(product);
    }

    private String saveImage(MultipartFile imageFile) {
        try {
            // Upewnij się, że katalog docelowy istnieje
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Zapisywanie pliku z unikalną nazwą
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteProduct(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        productOpt.ifPresent(product -> {
            product.setDeleted(true);
            productRepository.save(product);
        });
    }
}
