package com.stavre.tinyurl.configuration;

import com.stavre.tinyurl.authorization.LinkPermissionsEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableMethodSecurity
@Configuration
public class SecurityFilterChainConfig {

    private final LinkPermissionsEvaluator linkPermissionsEvaluator;

    @Bean
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        var expression = new DefaultMethodSecurityExpressionHandler();

        expression.setPermissionEvaluator(linkPermissionsEvaluator);

        return expression;
    }

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
                        auth
                                .requestMatchers("/redirect/**").permitAll()
                                .requestMatchers("/dashboard/**").authenticated()
                                .requestMatchers("/create-link/**").permitAll()
                                .requestMatchers("/update-link/**").authenticated()
                                .requestMatchers("/delete-link/**").authenticated()
                                .requestMatchers("/link-stats/**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/css/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/js/**").permitAll()
                                .anyRequest().denyAll())
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }
}
