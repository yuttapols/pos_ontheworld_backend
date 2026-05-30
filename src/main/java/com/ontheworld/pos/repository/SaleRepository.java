package com.ontheworld.pos.repository;

import com.ontheworld.pos.entity.Branch;
import com.ontheworld.pos.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface SaleRepository extends JpaRepository<Sale, UUID> {
    Optional<Sale> findByReceiptNumber(String receiptNumber);
    Page<Sale> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Sale> findByBranch(Branch branch, Pageable pageable);

    long countByBranchAndCreatedAtBetween(Branch branch, LocalDateTime from, LocalDateTime to);

    @Query("SELECT COALESCE(SUM(s.total), 0) FROM Sale s WHERE s.branch = :branch AND s.createdAt BETWEEN :from AND :to")
    BigDecimal sumTotalByBranchAndCreatedAtBetween(@Param("branch") Branch branch,
                                                   @Param("from") LocalDateTime from,
                                                   @Param("to") LocalDateTime to);
}
