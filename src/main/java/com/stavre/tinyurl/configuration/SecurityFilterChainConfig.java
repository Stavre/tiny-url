package com.stavre.tinyurl.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityFilterChainConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.formLogin(form -> form.defaultSuccessUrl("/user/dashboard"));


        http.csrf(c -> {
            c.ignoringRequestMatchers("/h2-console/**");

        });

        http.authorizeHttpRequests(auth ->
                        auth.requestMatchers("/user/**").authenticated()
                                .requestMatchers("/anonymous/**").permitAll()
                                .requestMatchers("/redirect/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/css/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/js/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .anyRequest().denyAll())
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }
}
