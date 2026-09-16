package com.example.taco_cloud.form;

import com.example.taco_cloud.data.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrationFormTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void toUserMapsAllFieldsAndEncodesPassword() {
        RegistrationForm form = new RegistrationForm();
        form.setUsername("testuser");
        form.setPassword("secret");
        form.setFullname("Test User");
        form.setStreet("123 Main St");
        form.setCity("Springfield");
        form.setState("IL");
        form.setZip("62704");
        form.setPhone("555-1234");

        User user = form.toUser(passwordEncoder);

        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isNotEqualTo("secret");
        assertThat(passwordEncoder.matches("secret", user.getPassword())).isTrue();
        assertThat(user.getFullname()).isEqualTo("Test User");
        assertThat(user.getStreet()).isEqualTo("123 Main St");
        assertThat(user.getCity()).isEqualTo("Springfield");
        assertThat(user.getState()).isEqualTo("IL");
        assertThat(user.getZip()).isEqualTo("62704");
        assertThat(user.getPhoneNumber()).isEqualTo("555-1234");
    }
}
