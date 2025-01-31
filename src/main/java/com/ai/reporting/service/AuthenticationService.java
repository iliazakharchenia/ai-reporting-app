package com.ai.reporting.service;

import com.ai.reporting.core.exception.CredentialsIncorrectException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class AuthenticationService {
    private final String username;
    private final String password;

    public Mono<Boolean> verify(String usernameToCompare, String passwordToCompare) {
        return Mono.just(usernameToCompare.equals(this.username)
                && passwordToCompare.equals(this.password))
                .map(isValid -> {
                    if (isValid) return true;
                    else throw new CredentialsIncorrectException("Credentials are incorrect");
                });
    }

    public AuthenticationService(
            @Value("${app.auth.basic_username}") String usernameString,
            @Value("${app.auth.basic_password}") String passwordString) {
        this.username = usernameString;
        this.password = Base64.getEncoder().encodeToString(passwordString.getBytes(StandardCharsets.UTF_8));
    }
}
