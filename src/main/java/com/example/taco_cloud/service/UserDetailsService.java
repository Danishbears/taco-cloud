package com.example.taco_cloud.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsService {
    UserDetails loadUsersByUsername(String name) throws UsernameNotFoundException;
}
