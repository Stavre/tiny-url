package com.stavre.tinyurl.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity
@Configuration
public class SecurityFilterChainConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.formLogin(form -> form.defaultSuccessUrl("/dashboard"));

        http.csrf(c ->
            c.ignoringRequestMatchers("/h2-console/**")
        );

        http.anonymous(anonymous ->
            anonymous.principal("guest").authorities("ROLE_ANONYMOUS").key("anonymous")
        );

        http.authorizeHttpRequests(auth ->
                        auth.requestMatchers("/user/**").authenticated()
                                .requestMatchers("/redirect/**").permitAll()
                                .requestMatchers("/dashboard/**").authenticated()
                                .requestMatchers("/create-link/**").permitAll()
                                .requestMatchers("/update-link/**").authenticated()
                                .requestMatchers("/delete-link/**").authenticated()
                                .requestMatchers("/link-stats//**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/css/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/js/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/debug").anonymous()
                                .requestMatchers("/favicon.ico").permitAll()
                                .requestMatchers("/*").anonymous()
                                .anyRequest().denyAll())
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }
}
