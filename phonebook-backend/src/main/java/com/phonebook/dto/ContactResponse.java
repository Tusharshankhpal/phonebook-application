package com.phonebook.dto;

import java.time.LocalDateTime;

public record ContactResponse(
    Long id,
    String name,
    String phoneNumber,
    String email,
    String address,
    LocalDateTime createdAt
) {
}
