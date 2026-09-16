package com.example.taco_cloud.data;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void userHasRoleUserAuthority() {
        User user = new User("testuser", "password", "Test User", "123 St", "City", "ST", "12345", "555");

        assertThat(user.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }
}
