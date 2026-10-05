package com.howtodoinjava.demo.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 7 configuration without WebSecurityConfigurerAdapter.
 * Every piece that used to be an overridden configure(...) method is a bean.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // replaces @EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  // 1. Replaces configure(HttpSecurity http)
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/recipes").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
        .httpBasic(withDefaults())
        .formLogin(withDefaults());
    return http.build();
  }

  // 2. Replaces configure(WebSecurity web) with web.ignoring()
  @Bean
  WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring().requestMatchers("/css/**", "/images/**");
  }

  // 3. Replaces configure(AuthenticationManagerBuilder auth) with inMemoryAuthentication()
  @Bean
  UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    UserDetails ravi = User.withUsername("ravi")
        .password(passwordEncoder.encode("pass"))
        .roles("USER")
        .build();
    UserDetails admin = User.withUsername("admin")
        .password(passwordEncoder.encode("pass"))
        .roles("USER", "ADMIN")
        .build();
    return new InMemoryUserDetailsManager(ravi, admin);
  }

  // 4. Replaces User.withDefaultPasswordEncoder()
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // 5. Replaces authenticationManagerBean() from the adapter
  @Bean
  AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return new ProviderManager(provider);
  }
}
