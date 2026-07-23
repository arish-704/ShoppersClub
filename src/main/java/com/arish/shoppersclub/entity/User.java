package com.arish.shoppersclub.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.arish.shoppersclub.enums.Role;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




/**
 * Represents an application user within the ShoppersClub platform.
 *
 * This entity stores user profile information, authentication
 * credentials, and assigned roles.
 *
 * 
 * The UserDetailsService is a core interface in Spring Security used to load user-specific data (such as username, password, and roles) from a data source during the authentication process.
 * It implements Spring Security's {@link UserDetails} interface,
 * allowing Spring Security to directly use this entity during
 * authentication and authorization without requiring a separate
 * security-specific user model.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity implements UserDetails {

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
    @ElementCollection(fetch = FetchType.EAGER) 
    @CollectionTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Builder.Default // --> Adding @Builder.Default makes builder-created users receive a real empty HashSet.
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false)
    private Boolean active =true;


    /**
     * Returns the collection of authorities granted to the authenticated user.
     *
     * The application's roles are converted into Spring Security
     * {@link GrantedAuthority} objects so that they can be used
     * by the authorization framework while evaluating access rules.
     *
     * @return collection of granted authorities assigned to the user.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())) // --> // Converts each application role into a Spring Security authority.
                    .collect(Collectors.toList());
    }



    /**
     * Returns the unique username used by Spring Security for authentication.
     *
     * In this application, the user's email address serves as the
     * login identifier instead of a traditional username.
     *
     * @return authenticated user's email address.
     */
    @Override
    public String getUsername() {
       return email;
    }
}
