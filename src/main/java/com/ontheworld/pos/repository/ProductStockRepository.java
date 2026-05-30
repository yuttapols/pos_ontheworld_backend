package com.ontheworld.pos.repository;

import com.ontheworld.pos.entity.Branch;
import com.ontheworld.pos.entity.Product;
import com.ontheworld.pos.entity.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductStockRepository extends JpaRepository<ProductStock, UUID> {
    Optional<ProductStock> findByProductAndBranch(Product product, Branch branch);
    List<ProductStock> findByBranch(Branch branch);
    List<ProductStock> findByProduct(Product product);
    long countByBranchAndQuantityEquals(Branch branch, int quantity);
}
