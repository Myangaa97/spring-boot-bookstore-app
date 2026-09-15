package com.bookstore.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.bookstore.security.LoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private LoginSuccessHandler loginSuccessHandler;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth.requestMatchers(

				"/", "/shop", "/shop/**", "/register", "/login", "/css/**", "/js/**", "/images/**", "/error").permitAll()

				.requestMatchers("/admin/**", "/books", "/authors", "/categories").hasRole("ADMIN")

				.requestMatchers("/api/users/**", "/api/books/**", "/api/authors/**", "/api/categories/**")
				.hasRole("ADMIN")

				.requestMatchers("/customer/**", "/api/cart/**", "/api/checkout/**", "/api/customer/**").hasRole("CUSTOMER")

				.anyRequest().authenticated()

		);

		http.formLogin(form -> form.loginPage("/login").successHandler(loginSuccessHandler));

		http.logout(logout -> logout.logoutSuccessUrl("/login?logout"));

		return http.build();
	}
}
