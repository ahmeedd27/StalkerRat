package com.ahmed.AhmedMohmoud.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true) // to handle role based authentication
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler lh;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth-> {
                 auth.requestMatchers(
                            "/register",
                            "search" ,
                            "user/**",
                            "search/**",
                            "/login",
                           "/swagger-ui.html/**",
                         "/swagger-ui/**"
                         ,"/v3/api-docs/**"
                    ).permitAll();
                 auth.anyRequest().authenticated();
                })
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter , UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(
                        s ->
                                s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(l ->
                       l.logoutUrl("/logout")
                               .addLogoutHandler(lh)
                               .logoutSuccessHandler(((request, response, authentication) ->
                                       SecurityContextHolder.clearContext()
                                       )
                               )
                        )
                .build();
    }

}
