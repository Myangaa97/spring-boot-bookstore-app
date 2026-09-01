package com.bookstore.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth.requestMatchers(

				"/register", "/css/**", "/js/**", "/images/**", "/api/health", "/error").permitAll()

				.requestMatchers("/admin/**", "/books", "/authors", "/categories").hasRole("ADMIN")

				.requestMatchers("/api/users/**", "/api/books/**", "/api/authors/**", "/api/categories/**")
				.hasRole("ADMIN")

				.requestMatchers("/customer/**").hasRole("CUSTOMER")

				.anyRequest().authenticated()

		);

		http.formLogin(org.springframework.security.config.Customizer.withDefaults());
		http.logout(logout -> logout.logoutSuccessUrl("/login?logout"));

		return http.build();
	}
}
