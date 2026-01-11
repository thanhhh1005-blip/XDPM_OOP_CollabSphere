package com.collabsphere.identity.controller;

import com.collabsphere.identity.dto.request.AuthenticationRequest;
import com.collabsphere.identity.dto.request.IntrospectRequest;
import com.collabsphere.identity.dto.response.AuthenticationResponse;
import com.collabsphere.identity.dto.response.IntrospectResponse;

// 👇 IMPORT MỚI
import com.collabsphere.identity.dto.response.ApiResponse; 

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

    // API Cũ - Giữ nguyên
    @PostMapping("/token")
    public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request){
        return authenticationService.authenticate(request);
    }

    // API Cũ - Giữ nguyên
    @PostMapping("/introspect")
    public IntrospectResponse introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        return authenticationService.introspect(request);
    }

    // 👇👇👇 API MỚI: Google Login Endpoint 👇👇👇
    @PostMapping("/outbound/authentication")
    public ApiResponse<AuthenticationResponse> outboundAuthenticate(@RequestParam("token") String token) {
        var result = authenticationService.outboundAuthenticate(token);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }
}