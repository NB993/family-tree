package io.jhchoe.familytree.common.support;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class ModifierBaseEntity extends CreatorBaseEntity {

    @LastModifiedBy
    @Column(name = "modified_by")
    private Long modifiedBy;

    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    public ModifierBaseEntity() {
    }

    public ModifierBaseEntity(final Long createdBy, final LocalDateTime createdAt, final Long modifiedBy, final LocalDateTime modifiedAt) {
        super(createdBy, createdAt);
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
    }
}
