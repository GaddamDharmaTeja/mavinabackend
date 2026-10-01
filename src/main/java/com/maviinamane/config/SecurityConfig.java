// package com.maviinamane.config;
// import com.maviinamane.auth.JwtService;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpHeaders;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.web.filter.OncePerRequestFilter;
// import java.util.List;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Bean
//     PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     SecurityFilterChain security(HttpSecurity http, JwtService jwt) throws Exception {
//         return http
//                 .csrf(csrf -> csrf.disable())
//                 .cors(cors -> {
//                 })
//                 .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                 .authorizeHttpRequests(a -> a
//                         .requestMatchers("/api/admin/**", "/api/uploads/**").hasRole("ADMIN")
//                         .requestMatchers("/api/orders/mine").hasRole("CUSTOMER")
//                         .requestMatchers("/api/farm-managers/login", "/api/farm-managers/register").permitAll()
//                         .requestMatchers("/api/farm-managers/**").hasRole("FARM_MANAGER")
//                         .anyRequest().permitAll())
//                 .addFilterBefore(new JwtFilter(jwt), UsernamePasswordAuthenticationFilter.class)
//                 .build();
//     }

//     @Bean
//     CorsConfigurationSource corsConfigurationSource() {
//         CorsConfiguration config = new CorsConfiguration();
//         config.setAllowedOrigins(List.of("http://localhost:3000"));
//         config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
//         config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", config);
//         return source;
//     }

//     static class JwtFilter extends OncePerRequestFilter {

//         private final JwtService jwt;

//         JwtFilter(JwtService jwt) {
//             this.jwt = jwt;
//         }

//         protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//                 throws java.io.IOException, jakarta.servlet.ServletException {

//             String header = request.getHeader(HttpHeaders.AUTHORIZATION);

//             if (header != null && header.startsWith("Bearer ")) {
//                 try {
//                     String token = header.substring(7);
//                     String email = jwt.email(token);
//                     var auth = new UsernamePasswordAuthenticationToken(
//                             email, null, List.of(new SimpleGrantedAuthority("ROLE_" + jwt.role(token))));
//                     org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
//                 } catch (Exception ignored) {
//                 }
//             }

//             chain.doFilter(request, response);
//         }
//     }
// }


package com.maviinamane.config;

import com.maviinamane.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.cors-origin}")
    private String corsOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain security(HttpSecurity http, JwtService jwt) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/auth/**",
                                "/api/products/**",
                                "/api/categories/**",
                                "/api/content/**",
                                "/api/orders/**",
                                "/uploads/**"
                        ).permitAll()

                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/payment/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/orders/mine")
                        .hasRole("CUSTOMER")

                        .requestMatchers(
                                "/api/farm-managers/login",
                                "/api/farm-managers/register")
                        .permitAll()

                        .requestMatchers("/api/farm-managers/**")
                        .hasRole("FARM_MANAGER")

                        .anyRequest()
                        .permitAll())

                .addFilterBefore(
                        new JwtFilter(jwt),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(
                Arrays.stream(corsOrigins.split(","))
                        .map(String::trim)
                        .filter(origin -> !origin.isBlank())
                        .toList());

        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"));

        config.setAllowedHeaders(List.of("*"));

        config.setExposedHeaders(List.of("*"));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    static class JwtFilter extends OncePerRequestFilter {

        private final JwtService jwt;

        JwtFilter(JwtService jwt) {
            this.jwt = jwt;
        }

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain chain)
                throws ServletException, IOException {

            String header = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (header != null && header.startsWith("Bearer ")) {
                try {

                    String token = header.substring(7);

                    String email = jwt.email(token);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    List.of(
                                            new SimpleGrantedAuthority(
                                                    "ROLE_" + jwt.role(token))));

                    org.springframework.security.core.context.SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);

                } catch (Exception ignored) {
                }
            }

            chain.doFilter(request, response);
        }
    }
}
