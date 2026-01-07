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

    // 👇 ĐÃ CẬP NHẬT: Dùng @Value để lấy key từ file cấu hình (application.yml)
    // Thay vì gán cứng trong code (Hardcode)
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @Autowired
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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
        // Tạo header với thuật toán HS512
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // Tạo payload (nội dung token)
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("collabsphere.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli() // Hết hạn sau 1 giờ
                ))
                .claim("userId", user.getId())
                .claim("scope", buildScope(user)) // Gọi hàm xử lý scope riêng cho gọn
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            // Sử dụng StandardCharsets.UTF_8 để đồng bộ encoding
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
            // Sử dụng StandardCharsets.UTF_8 để đồng bộ encoding
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes(StandardCharsets.UTF_8));
            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            var verified = signedJWT.verify(verifier);

            // Token chỉ hợp lệ khi: Chữ ký đúng VÀ Chưa hết hạn
            isValid = verified && expiryTime.after(new Date());

        } catch (JOSEException | ParseException e) {
            isValid = false;
        }

        return new IntrospectResponse(isValid);
    }

    // Hàm phụ để lấy Role convert sang String (tránh lỗi NullPointerException)
    private String buildScope(User user) {
        if (user.getRole() != null) {
            return user.getRole().name();
        }
        return "";
    }
}