package com.study.tasker.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    public ServerHttpSecurity serverHttpSecurity(ServerHttpSecurity security) {
        return security
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange((auth) ->
                        auth.pathMatchers(HttpMethod.GET, "/api/v1/user/**").hasAnyRole("MANAGER", "USER")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/user/**").hasAnyRole("MANAGER", "USER")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/user/**").hasAnyRole("MANAGER", "USER")
                        .pathMatchers(HttpMethod.POST, "/api/v1/user").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/task/**").hasAnyRole("MANAGER", "USER")
                        .pathMatchers(HttpMethod.GET, "/api/v1/task/add-obs/{id}").hasAnyRole("MANAGER", "USER")
                        .pathMatchers(HttpMethod.POST, "/api/v1/task").hasRole("MANAGER")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/task/**").hasRole("MANAGER")
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/task/add-obs/**").hasAnyRole("MANAGER", "USER")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/task/{id}").hasRole("MANAGER")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/task").permitAll()
                )
                .httpBasic(Customizer.withDefaults());
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                                                       PasswordEncoder passwordEncoder) {

        var manager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        manager.setPasswordEncoder(passwordEncoder);

        return manager;
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity security,
                                                      ReactiveAuthenticationManager manager) {

        return serverHttpSecurity(security).authenticationManager(manager).build();
    }
}
