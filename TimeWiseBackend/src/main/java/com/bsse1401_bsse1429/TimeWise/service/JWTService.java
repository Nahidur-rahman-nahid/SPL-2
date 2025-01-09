package com.bsse1401_bsse1429.TimeWise.service;

import com.bsse1401_bsse1429.TimeWise.model.User;
import com.bsse1401_bsse1429.TimeWise.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
    @Autowired
    UserRepository userRepo;

    @Value("${jwt.secret-key}")
    private String secretKey; // Injects the secret key from application.properties

    @Value("${jwt.token-expiration}")
    private long tokenExpiration; // Injects the token expiration time from application.properties

    // Generate JWT token using ObjectId (converted to String)
    public String generateToken(ObjectId userId) {
        try {
            // Log the user ID and expiration for debugging
            System.out.println("Generating token for userId: " + userId.toHexString());
            System.out.println("Token expiration: " + tokenExpiration);

            Map<String, Object> claims = new HashMap<>();
            return Jwts.builder().claims(claims)
                    .subject(userId.toHexString())  // Convert ObjectId to String
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + tokenExpiration))
                    .signWith(getKey())
                    .compact();
        } catch (Exception e) {
            // Log the error to understand what went wrong
            e.printStackTrace();
            throw new RuntimeException("Error generating JWT token", e);
        }
    }


    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Decode the secret key from BASE64
        return Keys.hmacShaKeyFor(keyBytes); // Generate SecretKey from the decoded byte array
    }

    // Extract userId from the token (convert the String back to ObjectId)
    public ObjectId extractUserId(String token) {
        return new ObjectId(extractClaim(token, Claims::getSubject)); // Convert the String back to ObjectId
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Validate token and match with UserDetails (comparing ObjectId)
    public boolean validateToken(String token, UserDetails userDetails) {
        final ObjectId userId = extractUserId(token);
        User user = userRepo.findByUserId(userId);
        if (user == null) {
            throw new RuntimeException("User not found with userId: " + userId.toHexString());
        }
      //  System.out.println("User found: " + user.getUserName());

        return (user.getUserId().equals(userId) && !isTokenExpired(token));

    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Verify token
    public boolean verifyToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date()); // Check if token is not expired
        } catch (Exception e) {
            return false; // Token is invalid
        }}
    }

