package com.ontheworld.pos.repository;

import com.ontheworld.pos.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByPhone(String phone);
    Optional<Customer> findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.nameTh) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(c.nameEn) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(c.phone)  LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(c.email)  LIKE LOWER(CONCAT('%',:q,'%'))")
    Page<Customer> searchByQuery(@Param("q") String query, Pageable pageable);
}
