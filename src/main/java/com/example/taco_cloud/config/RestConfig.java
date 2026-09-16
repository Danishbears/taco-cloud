package com.example.taco_cloud.config;

import com.example.taco_cloud.service.IngredientService;
import com.example.taco_cloud.service.RestIngredientService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class RestConfig {

    @Bean
    @RequestScope
    public IngredientService ingredientService(OAuth2AuthorizedClientManager clientManager) {
        OAuth2ClientHttpRequestInterceptor interceptor =
                new OAuth2ClientHttpRequestInterceptor(clientManager);

        interceptor.setClientRegistrationIdResolver(request -> "taco-admin-client");

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(interceptor);

        return new RestIngredientService(restTemplate);
    }
}