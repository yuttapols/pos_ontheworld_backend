package com.ontheworld.pos.repository;

import com.ontheworld.pos.entity.Customer;
import com.ontheworld.pos.entity.LoyaltyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, UUID> {
    List<LoyaltyTransaction> findByCustomerOrderByCreatedAtDesc(Customer customer);
}
