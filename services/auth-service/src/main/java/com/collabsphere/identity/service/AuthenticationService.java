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
import java.util.Optional; // Import thêm Optional

// 👇 IMPORTS MỚI CHO GOOGLE LOGIN
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.collabsphere.identity.enums.Role;
import java.util.UUID;

// 👇 IMPORT SERVICE GỬI EMAIL
import com.collabsphere.identity.service.EmailService; 

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // 1. Khai báo biến EmailService

    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    // 2. Cập nhật Constructor để Inject EmailService vào
    @Autowired
    public AuthenticationService(UserRepository userRepository, 
                                 PasswordEncoder passwordEncoder,
                                 EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // =========================================================================
    // CÁC HÀM CŨ (GIỮ NGUYÊN TUYỆT ĐỐI)
    // =========================================================================

    // 1. Hàm Đăng Nhập (Login) - GIỮ NGUYÊN
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated)
            throw new RuntimeException("Unauthenticated");

        if (!user.isActive()) {
            throw new RuntimeException("Tài khoản của bạn đã bị vô hiệu hóa! Vui lòng liên hệ Admin.");
        }

        var token = generateToken(user);

        return AuthenticationResponse.builder()
            .token(token)
            .authenticated(true)
            .user(new com.collab.shared.dto.UserDTO(
                user.getId(), 
                user.getFullName(), 
                user.getAvatarUrl(),
                user.getEmail(), 
                user.getRole().name()
            ))
            .build();
    }

    // 2. Hàm Tạo Token - GIỮ NGUYÊN
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

    // 3. Hàm Kiểm Tra Token (Introspect) - GIỮ NGUYÊN
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

    // =========================================================================
    // 4. HÀM MỚI: Xử lý Đăng nhập Google (CÓ THÊM GỬI EMAIL)
    // =========================================================================
    
    public AuthenticationResponse outboundAuthenticate(String token) {
        try {
            // Xác thực Token với Firebase
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

            // Lấy thông tin user
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            String picture = decodedToken.getPicture();

            // 👇 SỬA ĐỔI NHỎ: Tách logic kiểm tra user để biết khi nào cần gửi mail
            Optional<User> userOptional = userRepository.findByUsername(email);
            User user;

            if (userOptional.isPresent()) {
                // === USER CŨ (Đã tồn tại) ===
                user = userOptional.get();
            } else {
                // === USER MỚI (Chưa tồn tại) -> Tạo mới & Gửi Mail ===
                User newUser = new User();
                newUser.setUsername(email);
                newUser.setEmail(email);
                newUser.setFullName(name);
                newUser.setAvatarUrl(picture);
                newUser.setRole(Role.STUDENT);
                newUser.setActive(true);
                newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                
                user = userRepository.save(newUser);

                // >>> GỌI EMAIL SERVICE TẠI ĐÂY <<<
                emailService.sendWelcomeEmail(email, name);
            }
            
            // Kiểm tra khóa tài khoản (cho user cũ đăng nhập lại bằng Google)
            if (!user.isActive()) {
                throw new RuntimeException("Tài khoản Google này đã bị khóa trong hệ thống!");
            }

            // Tạo Token hệ thống (HS512)
            var internalToken = generateToken(user);
            
            // Logic trả về giữ nguyên như code bạn đưa
            return new AuthenticationResponse(internalToken, true);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi xác thực Google: " + e.getMessage());
        }
    }
}