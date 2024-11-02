package com.example.zai_store_proj.repository;

import com.example.zai_store_proj.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
