
package com.mamroune.meetingbook.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.mamroune.meetingbook.repository.AppUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class SecurityConfig {

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .cors(withDefaults())
                                .csrf(csrf -> csrf.spa())
                                .authorizeHttpRequests(auth -> auth
                                                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/meeting-rooms",
                                                                "/api/meeting-rooms/*")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/auth/csrf").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/reservations",
                                                                "/api/reservations/*")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/reservations")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.PATCH,
                                                                "/api/reservations/*/cancel")
                                                .authenticated()
                                                .anyRequest().denyAll())
                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint((request, response, exception) -> response
                                                                .sendError(401)))
                                .formLogin(form -> form
                                                .loginProcessingUrl("/api/auth/login")
                                                .usernameParameter("email")
                                                .successHandler((request, response, authentication) -> response
                                                                .setStatus(200))
                                                .failureHandler(
                                                                (request, response, exception) -> response.sendError(
                                                                                401, "Identifiants invalides")))
                                .logout(logout -> logout
                                                .logoutUrl("/api/auth/logout")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .logoutSuccessHandler((request, response, authentication) -> response
                                                                .setStatus(204)))
                                .httpBasic(AbstractHttpConfigurer::disable);

                return http.build();
        }

        @Bean
        PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        UserDetailsService userDetailsService(AppUserRepository repository) {
                return email -> repository.findByEmail(email)
                                .map(user -> User.withUsername(user.getEmail())
                                                .password(user.getPasswordHash())
                                                .roles("USER")
                                                .build())
                                .orElseThrow(() -> new UsernameNotFoundException("Identifiants invalides"));
        }

        @Bean
        AuthenticationManager authenticationManager(
                        UserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {

                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

                provider.setPasswordEncoder(passwordEncoder);

                return new ProviderManager(provider);
        }
}
