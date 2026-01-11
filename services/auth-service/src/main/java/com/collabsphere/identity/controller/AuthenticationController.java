package com.collabsphere.identity.controller;

<<<<<<< HEAD

=======
>>>>>>> origin/main
import com.collabsphere.identity.dto.request.AuthenticationRequest;
import com.collabsphere.identity.dto.request.IntrospectRequest;
import com.collabsphere.identity.dto.response.AuthenticationResponse;
import com.collabsphere.identity.dto.response.IntrospectResponse;

<<<<<<< HEAD
=======
// 👇 IMPORT MỚI
import com.collabsphere.identity.dto.response.ApiResponse; 

>>>>>>> origin/main
import com.collabsphere.identity.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

<<<<<<< HEAD
=======
    // API Cũ - Giữ nguyên
>>>>>>> origin/main
    @PostMapping("/token")
    public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request){
        return authenticationService.authenticate(request);
    }

<<<<<<< HEAD
=======
    // API Cũ - Giữ nguyên
>>>>>>> origin/main
    @PostMapping("/introspect")
    public IntrospectResponse introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        return authenticationService.introspect(request);
    }
<<<<<<< HEAD
=======

    // 👇👇👇 API MỚI: Google Login Endpoint 👇👇👇
    @PostMapping("/outbound/authentication")
    public ApiResponse<AuthenticationResponse> outboundAuthenticate(@RequestParam("token") String token) {
        var result = authenticationService.outboundAuthenticate(token);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }
>>>>>>> origin/main
}