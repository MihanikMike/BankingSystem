package com.mike.bank;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")

public class AuthController {

    private final JwtEncoder jwtEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthController(JwtEncoder jwtEncoder,
                          AuthenticationManager authenticationManager){

        this.jwtEncoder = jwtEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.getUsername(),
                        request.getPassword()
                ));

        Instant now = Instant.now();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(authentication.getName())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(900))
                .claim("roles", roles)
                .build();

        String token = jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();

        return Map.of("token", token);

    }

}
