package com.example.taco_cloud.data;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true) // Автоматически создает пустой конструктор для JPA
public class User implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private final String username;
    private final String password;
    private final String fullname;
    private final String street;
    private final String city;
    private final String state;
    private final String zip;
    private final String phoneNumber;

    // Конструктор со всеми полями
    public User(String username, String password, String fullname, String street, String city, String state, String zip, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.phoneNumber = phoneNumber;
    }

    // Упрощенный конструктор для DataLoader (как в книге)
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.fullname = null;
        this.street = null;
        this.city = null;
        this.state = null;
        this.zip = null;
        this.phoneNumber = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.password; // Возвращаем реальный пароль из поля!
    }

    @Override
    public String getUsername() {
        return this.username; // Возвращаем реальный логин из поля!
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}