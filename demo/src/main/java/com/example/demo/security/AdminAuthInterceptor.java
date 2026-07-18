package com.example.demo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.example.demo.entity.User;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

	private final AuthTokenService authTokenService;

	public AdminAuthInterceptor(AuthTokenService authTokenService) {
		this.authTokenService = authTokenService;
	}

	@Override
	public boolean preHandle(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull Object handler) throws Exception {
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			return true;
		}
		String auth = request.getHeader("Authorization");
		if (auth == null || !auth.startsWith("Bearer ")) {
			return unauthorized(response, "Authentication required");
		}
		String token = auth.substring(7).trim();
		try {
			AuthTokenService.TokenClaims claims = authTokenService.parseAndValidate(token);
			if (!User.ROLE_ADMIN.equals(claims.role())) {
				return forbidden(response, "Admin access required");
			}
			return true;
		}
		catch (Exception e) {
			return unauthorized(response, "Invalid or expired session");
		}
	}

	private static boolean unauthorized(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter().write("{\"message\":\"" + escapeJson(message) + "\"}");
		return false;
	}

	private static boolean forbidden(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter().write("{\"message\":\"" + escapeJson(message) + "\"}");
		return false;
	}

	private static String escapeJson(String s) {
		return s.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
