package com.ontheworld.pos.entity;

import com.ontheworld.pos.entity.base.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category extends SoftDeletableEntity {

    @Column(nullable = false)
    private String nameTh;

    @Column(nullable = false)
    private String nameEn;

    @Column(length = 250)
    private String descriptionTh;

    @Column(length = 250)
    private String descriptionEn;

    @Column(nullable = false)
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    public Category(String nameTh, String nameEn, String descriptionTh, String descriptionEn) {
        this.nameTh = nameTh;
        this.nameEn = nameEn;
        this.descriptionTh = descriptionTh;
        this.descriptionEn = descriptionEn;
    }
}
