package dev.interviewos.api.config;

import dev.interviewos.api.auth.OAuth2LoginSuccessHandler;
import dev.interviewos.api.auth.RedirectUriMemoFilter;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final FrontendProperties frontend;
    private final OAuth2LoginSuccessHandler successHandler;

    public SecurityConfig(
            FrontendProperties frontend,
            OAuth2LoginSuccessHandler successHandler) {

        this.frontend = frontend;
        this.successHandler = successHandler;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf ->
                        csrf.disable())

                .sessionManagement(s ->
                        s.sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED))

                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(
                                        "/oauth2/**",
                                        "/login/**",
                                        "/error"
                                ).permitAll()

                                .requestMatchers(
                                        "/api/auth/me",
                                        "/api/auth/logout",
                                        "/api/interviews/prep/ping"
                                ).permitAll()

                                .anyRequest().authenticated()
                )

                .oauth2Login(oauth ->
                        oauth
                                .successHandler(successHandler)

                                .failureHandler((req, res, ex) ->
                                        res.sendRedirect(
                                                frontend.baseUrl()
                                                        + "/?error=oauth"
                                        ))
                )

                .logout(logout ->
                        logout
                                .logoutUrl("/api/auth/logout")
                                .deleteCookies("JSESSIONID")
                                .logoutSuccessHandler(
                                        (req, res, auth) ->
                                                res.setStatus(
                                                        HttpStatus.NO_CONTENT.value()
                                                )
                                )
                )

                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(
                                new HttpStatusEntryPoint(
                                        HttpStatus.UNAUTHORIZED
                                )
                        )
                )

                .addFilterBefore(
                        new RedirectUriMemoFilter(),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config =
                new CorsConfiguration();

        config.setAllowedOrigins(
                List.of(frontend.baseUrl())
        );

        config.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        config.setAllowedHeaders(
                List.of("*")
        );

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                config
        );

        return source;
    }
}