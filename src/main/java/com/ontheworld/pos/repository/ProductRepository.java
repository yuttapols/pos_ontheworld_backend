package com.ontheworld.pos.repository;

import com.ontheworld.pos.entity.Product;
import com.ontheworld.pos.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findBySku(String sku);
    Optional<Product> findByBarcode(String barcode);
    Page<Product> findByCategory(Category category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.nameTh) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.nameEn) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.sku)    LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.barcode) LIKE LOWER(CONCAT('%',:q,'%'))")
    Page<Product> searchByQuery(@Param("q") String query, Pageable pageable);
}
