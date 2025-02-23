package com.example.zai_store_proj.repository;

import com.example.zai_store_proj.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select * from users where email = ?1", nativeQuery = true)
    Optional<User> findByEmail(String email);
}
