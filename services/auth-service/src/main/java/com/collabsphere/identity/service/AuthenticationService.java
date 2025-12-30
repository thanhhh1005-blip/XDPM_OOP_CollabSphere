package com.collabsphere.identity.service;

// 👇 4 DÒNG IMPORT QUAN TRỌNG NHẤT (Đã sửa lại đường dẫn)
import com.collabsphere.identity.dto.request.AuthenticationRequest;
import com.collabsphere.identity.dto.request.IntrospectRequest;
import com.collabsphere.identity.dto.response.AuthenticationResponse;
import com.collabsphere.identity.dto.response.IntrospectResponse;

import com.collabsphere.identity.entity.User;
import com.collabsphere.identity.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Key dùng để ký token (Nên lưu trong application.yml, tạm thời để đây)
    protected String SIGNER_KEY = "1234567890123456789012345678901212345678901234567890123456789012";

    // 1. Hàm Đăng Nhập (Login)
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated)
            throw new RuntimeException("Unauthenticated");

        var token = generateToken(user);

        return new AuthenticationResponse(token, true);
    }

    // 2. Hàm Tạo Token
    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("collabsphere.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("userId", user.getId())
                .claim("scope", user.getRole().name())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Cannot create token", e);
        }
    }

    // 3. Hàm Kiểm Tra Token (Introspect)
    public IntrospectResponse introspect(IntrospectRequest request) {
        var token = request.getToken();
        boolean isValid = true;

        try {
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            var verified = signedJWT.verify(verifier);

            isValid = verified && expiryTime.after(new Date());

        } catch (JOSEException | ParseException e) {
            isValid = false;
        }

        return new IntrospectResponse(isValid);
    }
}