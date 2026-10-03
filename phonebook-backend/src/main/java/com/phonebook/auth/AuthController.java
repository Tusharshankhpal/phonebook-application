package com.phonebook.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AdminRepository adminRepository;

    public AuthController(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        if (request.getUsername() == null
                || request.getPassword() == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            new LoginResponse(
                                    false,
                                    "Invalid username or password",
                                    null
                            )
                    );
        }

        String username = request.getUsername().trim();

        Admin admin = adminRepository
                .findByUsername(username)
                .orElse(null);

        if (admin == null
                || !admin.getPassword().equals(request.getPassword())) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            new LoginResponse(
                                    false,
                                    "Invalid username or password",
                                    null
                            )
                    );
        }

        return ResponseEntity.ok(
                new LoginResponse(
                        true,
                        "Login successful",
                        admin.getUsername()
                )
        );
    }
}