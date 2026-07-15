package com.arish.shoppersclub.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;




/**
 * ============================================================================
 * BaseEntity
 * ============================================================================
 *
 * Purpose:
 * A parent class that provides common fields shared by all entities.
 *
 * Why use it?
 * - Avoids duplicating id, createdAt and updatedAt in every entity.
 * - Provides automatic auditing using JPA lifecycle callbacks.
 *
 * JPA Concepts Revised:
 * - @MappedSuperclass
 * - @Id
 * - @GeneratedValue
 * - @PrePersist
 * - @PreUpdate
 *
 * Note:
 * This class is NOT an entity and therefore Hibernate will not create
 * a separate table for it. Instead, its fields are inherited by child
 * entities.
 * ============================================================================
 */
@Getter
@Setter
@MappedSuperclass // --> This annotation is used to let us know This class is not an entity itself. It exists only to share common fields with child entities.
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Don't generate the ID yourself. Let the database handle it.
    private Long id; // --> Use wrapper classes (Long, Integer, Boolean) for entity fields because they can represent null, which has meaning in persistence.
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    
    @Column(nullable =false)
    private LocalDateTime updatedAt;





    /**
     * Executed automatically by JPA/Hibernate just before the entity
     * is inserted into the database.
     *
     * Used to initialize audit fields like createdAt and updatedAt.
     * This keeps timestamp logic inside the entity instead of repeating
     * it in every service or controller.
     */
    @PrePersist
    public void onCreating(){
        LocalDateTime now = LocalDateTime.now();
        if(this.createdAt == null){
            this.createdAt = now; 
        }

        this.updatedAt = now;
    }




    /**
     * Executed automatically by JPA/Hibernate just before an existing
     * entity is updated in the database.
     *
     * Refreshes the updatedAt timestamp so it always reflects the
     * last modification time of the entity.
     */
    @PreUpdate
    public void onUpdating(){
        
        this.updatedAt = LocalDateTime.now();
        
    }


}
