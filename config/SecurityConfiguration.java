package com.example.demo.config;

import static com.example.demo.model.user.enumerated.Role.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    //url della white list sono endpoint pubblici, non richiedono un token jwt
    private final String[] WHITE_LIST_URL = {"/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/api/v1/debug/**"
        };
    private final LogoutHandler logoutHandler;

    //noi abbiamo sempre una white list, con gli end point che non richiedono il token
    //esempio -> creo/log in, non mi serve token. Se creo, il token mi serve dopo.

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(req -> 
                req.requestMatchers(WHITE_LIST_URL)
                        .permitAll()

                        /* TODO -> in basa a quello che facciamo nei vari controller, implementiamo il corpo
                        .requestMatchers("/api/v1/users/**").hasAnyRole(USER.name(), ADMIN.name(), MANAGER.name())
                        .requestMatchers("/api/v1/favorite/**").hasRole(USER.name())
                        .requestMatchers("/api/v1/offer/**").hasAnyRole(USER.name(), ADMIN.name(), MANAGER.name())
                        .requestMatchers("/api/v1/property/**").hasAnyRole(USER.name(), ADMIN.name(), MANAGER.name())
                        .requestMatchers("/api/v1/visit/**").hasAnyRole(USER.name(), ADMIN.name(), MANAGER.name())
                        //posso accedere a qualunque endpoint sotto api/v1/management, devo essere ROLE_ADMIN o ROLE_MANAGER
                        .requestMatchers("/api/v1/management/**").hasAnyRole(ADMIN.name(), MANAGER.name())
                        //posso leggere solo se ho *_READ
                        .requestMatchers(HttpMethod.GET, "/api/v1/management/**").hasAnyAuthority(PROPERTY_READ.getPermission())
                        //posso creare solo se ho *_CREATE
                        .requestMatchers(HttpMethod.POST, "/api/v1/management/**").hasAnyAuthority(PROPERTY_CREATE.getPermission())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/management/**").hasAnyAuthority(PROPERTY_UPDATE.getPermission())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/management/**").hasAnyAuthority(PROPERTY_DELETE.getPermission())
                        //qualunque altra richiesta è protetta. Se non è in white list, serve un jwt valido
                        .anyRequest()
                        .authenticated()
                        */
                       .requestMatchers("/api/**").hasAnyRole(USER.name(), TELLER.name(), BANK_MANAGER.name())
                       .anyRequest()
                       .authenticated()
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout ->
                logout.logoutUrl("/api/v1/auth/logout")
                      .addLogoutHandler(logoutHandler)
                      //logout handler -> invalida token, lo mette in blacklist, e lo cancella dal lato client
                      .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
            );

        return http.build();
    }
}
