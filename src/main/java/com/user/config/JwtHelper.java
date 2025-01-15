package com.user.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.user.repository.JwtRepository;
import com.user.repository.UserRepository;
import com.user.repository.entity.JwtToken;
import com.user.repository.entity.Role;
import com.user.repository.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtHelper {
	@Autowired
	private JwtRepository jwtRepository;
	@Autowired
	private CustomDetailsService customDetailsService;
	@Autowired
	private UserRepository userRepository;
	// requirement :
	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	// public static final long JWT_TOKEN_VALIDITY = 60;
	private static String secret = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

	// retrieve username from jwt token
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	// for retrieveing any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	// check if the token has expired
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	// generate token for user
	public String generateToken(UserDetails userDetails, String password,Role role) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("Password", password);
		claims.put("role", role);
		return doGenerateToken(claims, userDetails.getUsername());
	}

	// while creating the token -
	// 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
	// 2. Sign the JWT using the HS512 algorithm and secret key.
	// 3. According to JWS Compact
	// Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	// compaction of the JWT to a URL-safe string
	private String doGenerateToken(Map<String, Object> claims, String subject) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	// validate token
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	public static Claims decodeJwt(String token) {
		try {
			return Jwts.parser().setSigningKey(secret)
					// Use the same signing key
					.setAllowedClockSkewSeconds(60).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			e.printStackTrace();
			return null; // Return null if token is invalid
		}
	}

	public String getOrGenerateToken(String userEmail, String password,Role role) {
		JwtToken existingToken = jwtRepository.findByEmail(userEmail);

		if (existingToken != null) {
			Date now = new Date();
			if (existingToken.getExpiresAt().after(now)) {
				// Token is still valid, return it
				return existingToken.getToken();
			} else {
				// Token is expired, remove it
				jwtRepository.delete(existingToken);
			}
		}

		// Token does not exist or is expired, generate a new one
		UserDetails details = customDetailsService.loadUserByUsername(userEmail);
		String newToken = generateToken(details, password,role);
		saveJwtToken(userEmail, newToken);
		return newToken;
	}

	private void saveJwtToken(String userEmail, String token) {
		Date issuedAt = new Date();
		Date expiresAt = new Date(issuedAt.getTime() + JwtHelper.JWT_TOKEN_VALIDITY * 1000);
		User user = userRepository.findByEmail(userEmail);

		JwtToken existingToken = jwtRepository.findByEmail(userEmail);
		if (existingToken != null) {
			// Update existing token
			existingToken.setToken(token);
			existingToken.setIssuedAt(issuedAt);
			existingToken.setExpiresAt(expiresAt);
			jwtRepository.save(existingToken);
		} else {
			// Save a new token
			JwtToken jwtToken = JwtToken.builder().email(userEmail).issuedAt(issuedAt).token(token).expiresAt(expiresAt)
					.user(user).build();
			jwtRepository.save(jwtToken);
		}
	}

}
