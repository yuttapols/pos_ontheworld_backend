package com.ontheworld.pos.repository;

import com.ontheworld.pos.entity.Branch;
import com.ontheworld.pos.entity.Role;
import com.ontheworld.pos.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
    Optional<UserAccount> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    List<UserAccount> findByBranchAndRoleInAndDeletedAtIsNull(Branch branch, Collection<Role> roles);
    long countByBranchAndRoleInAndDeletedAtIsNull(Branch branch, Collection<Role> roles);
}
