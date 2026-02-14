package com.example.demo.service.card;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CvvHasher {
    private final PasswordEncoder encoder;

    public String hash(String cvv) {
        return encoder.encode(cvv);
    }

    public boolean matches(String rawCvv, String hash) {
        return encoder.matches(rawCvv, hash);
    }
}
