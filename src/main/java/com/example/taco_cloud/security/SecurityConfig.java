package com.example.taco_cloud.security;


import com.example.taco_cloud.data.User;
import com.example.taco_cloud.jdbc.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // КЛЮЧЕВОЕ ИСПРАВЛЕНИЕ: первая цепочка обрабатывает ТОЛЬКО запросы к /api/**
                .securityMatcher("/api/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/ingredients")
                        .hasAuthority("SCOPE_writeIngredients")

                        .requestMatchers(HttpMethod.DELETE, "/api/ingredients/**")
                        .hasAuthority("SCOPE_deleteIngredients")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Отключаем CSRF для локального тестирования
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/design", "/orders").hasRole("USER")
                        .requestMatchers("/", "/**").permitAll()
                )
                .formLogin(Customizer.withDefaults())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/taco-admin-client") // Кастомный URL авторизации из книги
                        .defaultSuccessUrl("/design")
                )
                .oauth2Client(Customizer.withDefaults()) // Добавлен .oauth2Client(withDefaults()) из книги
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepo) {
        return username -> {
            User user = userRepo.findByUsername(username);
            if (user != null) return user;
            throw new UsernameNotFoundException("User '" + username + "' not found");
        };
    }
}