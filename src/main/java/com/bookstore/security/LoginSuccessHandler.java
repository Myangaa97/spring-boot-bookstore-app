package com.bookstore.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		boolean isAdmin = authentication.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch(authority -> authority.equals("ROLE_ADMIN"));
		
		if (isAdmin) {
			response.sendRedirect("/admin");
			return;
		}
		
		boolean isCustomer = authentication.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch(authority -> authority.equals("ROLE_CUSTOMER"));
		
		if (isCustomer) {
			response.sendRedirect("/customer");
			return;
		}
		
		response.sendRedirect("/");
		
	}
	
	
}
