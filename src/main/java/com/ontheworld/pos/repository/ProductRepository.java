package com.ontheworld.pos.repository;

import com.ontheworld.pos.entity.Branch;
import com.ontheworld.pos.entity.Category;
import com.ontheworld.pos.entity.Product;
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

    // Duplicate name+category check (create)
    boolean existsByNameThAndCategoryAndDeletedAtIsNull(String nameTh, Category category);

    // Duplicate name+category check (update — exclude current product)
    boolean existsByNameThAndCategoryAndIdNotAndDeletedAtIsNull(String nameTh, Category category, UUID excludeId);

    /** Scoped list: global (branch IS NULL) OR belongs to the given branch */
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND (p.branch IS NULL OR p.branch = :branch)")
    Page<Product> findByBranchScope(@Param("branch") Branch branch, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND (p.branch IS NULL OR p.branch = :branch) AND " +
           "(LOWER(p.nameTh) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(p.nameEn) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(p.barcode) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<Product> searchByBranchScope(@Param("branch") Branch branch, @Param("q") String query, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND (p.branch IS NULL OR p.branch = :branch) AND p.category = :category")
    Page<Product> findByCategoryAndBranchScope(@Param("category") Category category, @Param("branch") Branch branch, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.nameTh) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.nameEn) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.sku)    LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.barcode) LIKE LOWER(CONCAT('%',:q,'%'))")
    Page<Product> searchByQuery(@Param("q") String query, Pageable pageable);
}
