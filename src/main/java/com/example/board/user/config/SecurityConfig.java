package com.example.board.user.config;

import com.example.board.security.JwtAccessDeniedHandler;
import com.example.board.security.JwtAuthenticationEntryPoint;
import com.example.board.security.JwtFilter;
import com.example.board.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsConfigure -> corsConfigure.configurationSource(corsConfigurationSource()))
                .exceptionHandling(customizer -> customizer.accessDeniedHandler(new JwtAccessDeniedHandler()))
                .exceptionHandling(customizer -> customizer.authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PERMIT_ALL_LIST).permitAll()
                        .anyRequest().permitAll() // permitAll - authenticated
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

//                .logout(logout -> logout
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout", "POST"))
//                        .logoutSuccessUrl("/user/login?logout")
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                        .permitAll()
//                );

        return http.build();
    }

    private static final String[] PERMIT_ALL_LIST = {
            "/user/register",
            "/user/login",
            "/user/logout",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/JwtTokenUse/logout"
    };

    CorsConfigurationSource corsConfigurationSource() {
        final var configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("https://*");
        configuration.addAllowedOriginPattern("http://*:*");
        configuration.addAllowedOriginPattern("https://*:*");
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));

        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }


}
