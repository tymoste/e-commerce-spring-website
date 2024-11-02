package com.example.zai_store_proj.repository;

import com.example.zai_store_proj.model.Cart;
import com.example.zai_store_proj.model.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT ci FROM CartItem ci WHERE ci.id = :cartItemId")
    CartItem findCartItemById(@Param("cartItemId") Long cartItemId);

    @EntityGraph(attributePaths = {"cartItems"})
    Cart findWithItemsById(Long id);
}
