package com.ontheworld.pos.repository;

import com.ontheworld.pos.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {
    Optional<Branch> findByNameTh(String nameTh);
}
