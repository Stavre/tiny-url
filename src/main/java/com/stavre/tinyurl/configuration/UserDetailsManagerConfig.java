package com.stavre.tinyurl.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@RequiredArgsConstructor
public class UserDetailsManagerConfig {

    @Bean
    public UserDetailsManager userDetailsService(DataSource dataSource) {
        var useDetails = new JdbcUserDetailsManager(dataSource);

        String usersByUsernameQuery = "select username, password, enabled from users where username = ?";
        String authsByUserQuery = "select username, authority from authorities where username = ?";

        useDetails.setUsersByUsernameQuery(usersByUsernameQuery);
        useDetails.setAuthoritiesByUsernameQuery(authsByUserQuery);


        return useDetails;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
