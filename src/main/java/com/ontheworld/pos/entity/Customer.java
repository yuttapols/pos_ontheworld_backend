package com.ontheworld.pos.entity;

import com.ontheworld.pos.entity.base.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "customers")
public class Customer extends SoftDeletableEntity {

    @Column(nullable = false)
    private String nameTh;

    @Column(nullable = false)
    private String nameEn;

    @Column(unique = true)
    private String phone;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private Integer loyaltyPoints = 0;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Sale> sales = new HashSet<>();

    public Customer(String nameTh, String nameEn, String phone, String email) {
        this.nameTh = nameTh;
        this.nameEn = nameEn;
        this.phone = phone;
        this.email = email;
    }
}
