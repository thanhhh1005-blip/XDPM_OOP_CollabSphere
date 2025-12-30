package com.collabsphere.identity.controller;


import com.collabsphere.identity.dto.request.AuthenticationRequest;
import com.collabsphere.identity.dto.request.IntrospectRequest;
import com.collabsphere.identity.dto.response.AuthenticationResponse;
import com.collabsphere.identity.dto.response.IntrospectResponse;

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

    @PostMapping("/token")
    public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request){
        return authenticationService.authenticate(request);
    }

    @PostMapping("/introspect")
    public IntrospectResponse introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        return authenticationService.introspect(request);
    }
}