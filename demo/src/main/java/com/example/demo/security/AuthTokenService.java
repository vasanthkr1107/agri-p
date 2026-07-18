package com.example.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class AuthTokenService {

	private static final String HMAC = "HmacSHA256";
	private static final long TTL_MS = 7L * 24 * 60 * 60 * 1000;

	@Value("${app.auth.secret}")
	private String secret;

	public String createToken(long userId, String role) {
		long exp = System.currentTimeMillis() + TTL_MS;
		String payload = userId + "|" + role + "|" + exp;
		String sig = sign(payload);
		String combined = payload + "." + sig;
		return Base64.getUrlEncoder().withoutPadding().encodeToString(combined.getBytes(StandardCharsets.UTF_8));
	}

	public TokenClaims parseAndValidate(String token) {
		if (token == null || token.isBlank()) {
			throw new IllegalArgumentException("Missing token");
		}
		String combined = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
		int dot = combined.lastIndexOf('.');
		if (dot < 0) {
			throw new IllegalArgumentException("Invalid token");
		}
		String payload = combined.substring(0, dot);
		String sig = combined.substring(dot + 1);
		String expected = sign(payload);
		if (!constantTimeEquals(sig, expected)) {
			throw new IllegalArgumentException("Invalid signature");
		}
		String[] parts = payload.split("\\|", 3);
		if (parts.length != 3) {
			throw new IllegalArgumentException("Invalid payload");
		}
		long userId = Long.parseLong(parts[0]);
		String role = parts[1];
		long exp = Long.parseLong(parts[2]);
		if (System.currentTimeMillis() > exp) {
			throw new IllegalArgumentException("Token expired");
		}
		return new TokenClaims(userId, role);
	}

	private String sign(String payload) {
		try {
			Mac mac = Mac.getInstance(HMAC);
			mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC));
			byte[] raw = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
			return Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
		}
		catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new IllegalStateException("Cannot sign token", e);
		}
	}

	private static boolean constantTimeEquals(String a, String b) {
		if (a == null || b == null || a.length() != b.length()) {
			return false;
		}
		int r = 0;
		for (int i = 0; i < a.length(); i++) {
			r |= a.charAt(i) ^ b.charAt(i);
		}
		return r == 0;
	}

	public record TokenClaims(long userId, String role) {
	}
}
