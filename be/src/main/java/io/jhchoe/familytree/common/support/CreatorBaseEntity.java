package io.jhchoe.familytree.common.support;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class CreatorBaseEntity {

    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public CreatorBaseEntity() {
    }

    public CreatorBaseEntity(final Long createdBy, final LocalDateTime createdAt) {
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
}
