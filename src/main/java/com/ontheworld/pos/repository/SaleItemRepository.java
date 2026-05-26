package com.ontheworld.pos.repository;

import com.ontheworld.pos.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {
}
