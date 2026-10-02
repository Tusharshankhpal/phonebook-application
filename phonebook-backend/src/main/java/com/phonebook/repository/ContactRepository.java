package com.phonebook.repository;

import com.phonebook.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    Page<Contact> findByNameContainingIgnoreCaseOrPhoneNumberContaining(
        String name,
        String phoneNumber,
        Pageable pageable
    );
}
