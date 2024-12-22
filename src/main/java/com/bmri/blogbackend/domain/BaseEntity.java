package com.bmri.blogbackend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Getter
    @Setter(AccessLevel.NONE)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /* TODO: Once authentication is implemented, implement an AuditorAware to retrieve the current user,
        then uncomment createdBy and updatedBy */
    /*@Getter
    @Setter(AccessLevel.NONE)
    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;*/

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /*@LastModifiedBy
    @Column(nullable = false)
    private String updatedBy;*/
}
