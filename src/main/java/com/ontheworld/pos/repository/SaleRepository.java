package com.ontheworld.pos.repository;

import com.ontheworld.pos.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface SaleRepository extends JpaRepository<Sale, UUID> {
    Optional<Sale> findByReceiptNumber(String receiptNumber);
    Page<Sale> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
}
