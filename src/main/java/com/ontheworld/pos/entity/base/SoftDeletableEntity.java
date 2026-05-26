package com.ontheworld.pos.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@Where(clause = "deleted_at IS NULL")
public abstract class SoftDeletableEntity extends BaseEntity {

    @Column
    private LocalDateTime deletedAt;

    @Column(length = 100)
    private String deletedBy;

    public void softDelete(String performedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = performedBy;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
