package com.phonebook.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "contacts",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_contacts_phone_number",
            columnNames = "phone_number"
        ),
        @UniqueConstraint(
            name = "uk_contacts_email",
            columnNames = "email"
        )
    }
)
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(
        name = "phone_number",
        nullable = false,
        unique = true,
        length = 30
    )
    private String phoneNumber;

    @Column(unique = true, length = 255)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Contact() {
    }

    public Contact(
            String name,
            String phoneNumber,
            String email,
            String address) {

        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
