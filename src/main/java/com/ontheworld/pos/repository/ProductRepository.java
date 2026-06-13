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

    Optional<Product> findBySkuAndBranch(String sku, Branch branch);
    Optional<Product> findByBarcodeAndBranch(String barcode, Branch branch);

    /** Strict branch-only list */
    Page<Product> findByBranchAndDeletedAtIsNull(Branch branch, Pageable pageable);

    /** Strict branch + category filter */
    Page<Product> findByBranchAndCategoryAndDeletedAtIsNull(Branch branch, Category category, Pageable pageable);

    /** Strict branch search by name / sku / barcode */
    @Query("SELECT p FROM Product p WHERE p.branch = :branch AND p.deletedAt IS NULL AND " +
           "(LOWER(p.nameTh) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.nameEn) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.sku)    LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.barcode) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<Product> searchByBranch(@Param("branch") Branch branch, @Param("q") String q, Pageable pageable);

    /** Strict branch + category search */
    @Query("SELECT p FROM Product p WHERE p.branch = :branch AND p.category = :category AND p.deletedAt IS NULL AND " +
           "(LOWER(p.nameTh) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.nameEn) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.sku)    LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.barcode) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<Product> searchByBranchAndCategory(@Param("branch") Branch branch,
                                             @Param("category") Category category,
                                             @Param("q") String q,
                                             Pageable pageable);

    boolean existsByNameThAndCategoryAndDeletedAtIsNull(String nameTh, Category category);
    boolean existsByNameThAndCategoryAndIdNotAndDeletedAtIsNull(String nameTh, Category category, UUID excludeId);

    /** Legacy: global OR branch */
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND (p.branch IS NULL OR p.branch = :branch)")
    Page<Product> findByBranchScope(@Param("branch") Branch branch, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND " +
           "(LOWER(p.nameTh) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.nameEn) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.sku)    LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.barcode) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<Product> searchByQuery(@Param("q") String query, Pageable pageable);

    Page<Product> findByCategory(Category category, Pageable pageable);
}
