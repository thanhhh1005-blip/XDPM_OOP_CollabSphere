package com.collabsphere.identity.service;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @Autowired
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. Hàm Đăng Nhập (Login)
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Tìm user theo username
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra mật khẩu
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated)
            throw new RuntimeException("Unauthenticated");

        // 👇👇👇 QUAN TRỌNG: Kiểm tra tài khoản có bị khóa không 👇👇👇
        if (!user.isActive()) {
            throw new RuntimeException("Tài khoản của bạn đã bị vô hiệu hóa! Vui lòng liên hệ Admin.");
        }
        // 👆👆👆 HẾT PHẦN KIỂM TRA 👆👆👆

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
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes(StandardCharsets.UTF_8)));
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
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes(StandardCharsets.UTF_8));
            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            var verified = signedJWT.verify(verifier);

            isValid = verified && expiryTime.after(new Date());

        } catch (JOSEException | ParseException e) {
            isValid = false;
        }

        return new IntrospectResponse(isValid);
    }

    private String buildScope(User user) {
        if (user.getRole() != null) {
            return user.getRole().name();
        }
        return "";
    }
}