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
@Table(name = "branches")
public class Branch extends SoftDeletableEntity {

    @Column(nullable = false, unique = true)
    private String nameTh;

    @Column(nullable = false)
    private String nameEn;

    @Column(nullable = false)
    private String addressTh;

    @Column(nullable = false)
    private String addressEn;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserAccount> users = new HashSet<>();

    public Branch(String nameTh, String nameEn, String addressTh, String addressEn) {
        this.nameTh = nameTh;
        this.nameEn = nameEn;
        this.addressTh = addressTh;
        this.addressEn = addressEn;
    }
}
