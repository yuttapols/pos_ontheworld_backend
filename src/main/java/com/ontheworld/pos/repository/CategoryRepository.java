package com.ontheworld.pos.repository;

import com.ontheworld.pos.entity.Branch;
import com.ontheworld.pos.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByNameTh(String nameTh);

    /** Scoped list: global (branch IS NULL) OR belongs to the given branch */
    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NULL AND (c.branch IS NULL OR c.branch = :branch)")
    List<Category> findByBranchScope(@Param("branch") Branch branch);

    /** Duplicate name check within the same branch scope */
    boolean existsByNameThAndBranchAndDeletedAtIsNull(String nameTh, Branch branch);
    boolean existsByNameThAndBranchIsNullAndDeletedAtIsNull(String nameTh);
}
