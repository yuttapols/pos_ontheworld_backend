package com.ontheworld.pos.repository;

import com.ontheworld.pos.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
}
