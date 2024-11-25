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
                        authorize -> authorize.requestMatchers("/login/**", "/callback/login", "/error", "/favicon.ico").permitAll()
                                .requestMatchers("/token").permitAll()
                                .requestMatchers("/v3/boards/{id}/invitation").hasAuthority("INVITATION")
                                .requestMatchers("/v3/boards/{id}/invitation/{collabId}").hasAuthority("OWNER")
                                .requestMatchers(HttpMethod.GET, "/v3/boards/**").hasAnyAuthority("PUBLIC-ACCESS", "OWNER", "COLLABORATOR-READER", "COLLABORATOR-WRITER")
                                .requestMatchers(HttpMethod.GET, "/v3/boards").hasAnyAuthority("OWNER", "COLLABORATOR-READER", "COLLABORATOR-WRITER")
                                .requestMatchers(HttpMethod.POST, "/v3/boards").hasAuthority("OWNER")
                                .requestMatchers(HttpMethod.POST, "/v3/boards/{id}/collabs").hasAuthority("OWNER")
                                .requestMatchers(HttpMethod.PATCH, "/v3/boards/{id}").hasAuthority("OWNER")
                                .requestMatchers(HttpMethod.PATCH, "/v3/boards/{id}/collabs/{collabId}").hasAuthority("OWNER")
                                .requestMatchers(HttpMethod.DELETE, "/v3/boards/{id}/collabs/{collabId}").hasAnyAuthority("OWNER", "COLLABORATOR-WRITER", "COLLABORATOR-READER")
                                .anyRequest().hasAnyAuthority("OWNER", "COLLABORATOR-WRITER")
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
