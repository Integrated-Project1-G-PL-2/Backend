package com.itbangmodkradankanbanapi;

import com.itbangmodkradankanbanapi.db2.services.JwtAnonymousAuthFilter;
import com.itbangmodkradankanbanapi.db2.services.JwtAuthFilter;
import com.itbangmodkradankanbanapi.db2.services.JwtUserDetailsService;
import com.itbangmodkradankanbanapi.exception.CustomAccessDeniedHandler;
import com.itbangmodkradankanbanapi.exception.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    JwtAuthFilter jwtAuthFilter;

    @Autowired
    JwtAnonymousAuthFilter jwtAnonymousAuthFilter;

    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    CustomAccessDeniedHandler accessDeniedHandler;

    @Autowired
    CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable())
                .authorizeRequests(
                        authorize -> authorize.requestMatchers("/login", "/error").permitAll()
                                .requestMatchers("/token").permitAll()
                                .requestMatchers(HttpMethod.GET).hasAnyAuthority("PUBLIC_ACCESS", "OWNER")
                                .anyRequest().hasAuthority("OWNER")
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAnonymousAuthFilter, JwtAuthFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .httpBasic(withDefaults());
        return httpSecurity.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(jwtUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
