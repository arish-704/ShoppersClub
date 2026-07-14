package com.arish.shoppersclub.entity;

import java.util.HashSet;
import java.util.Set;

import com.arish.shoppersclub.enums.Role;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false)
    @NotBlank
    private String firstName;

    private String lastName;

    @Column(nullable = false , unique = true , length = 255)
    @Email
    @NotBlank
    private String email;

    @Column(nullable = false , length = 255)
    @NotBlank
    private String password;

    // @Column(nullable = false) --> here we are not using the column annotation as it will not be stored in the same table as a column it will be made into a new table to store the value.
    //This isn't a relationship to another entity, it's a collection of basic values (enums, strings, embeddables, etc.) that needs its own table.    
    @ElementCollection 
    @CollectionTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false)
    private Boolean active =true;
}
