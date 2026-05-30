package com.ontheworld.pos.entity;

public enum Role {
    ADMIN,         // Super admin — all branches, all data
    BRANCH_ADMIN,  // Admin of a specific branch — full view within their branch
    MANAGER,       // Sees CASHIER in their branch only
    CASHIER,       // Sees only themselves
    MEMBER         // Cross-branch customer, earns loyalty points anywhere
}
