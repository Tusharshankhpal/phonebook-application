package com.phonebook.service;

import com.phonebook.dto.ContactRequest;
import com.phonebook.dto.ContactResponse;
import com.phonebook.entity.Contact;
import com.phonebook.exception.ContactNotFoundException;
import com.phonebook.exception.DuplicateContactException;
import com.phonebook.repository.ContactRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Transactional(readOnly = true)
    public List<ContactResponse> getAllContacts() {

        return contactRepository.findAll()
            .stream()
            .map(this::convertToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public Page<ContactResponse> searchContacts(String search, Pageable pageable) {

        String query = search == null ? "" : search.trim();

        return contactRepository
            .findByNameContainingIgnoreCaseOrPhoneNumberContaining(
                query, query, pageable
            )
            .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public ContactResponse getContactById(Long id) {

        Contact contact = findContactById(id);

        return convertToResponse(contact);
    }

    public ContactResponse createContact(ContactRequest request) {

        String phoneNumber = request.getPhoneNumber().trim();
        String email = normalize(request.getEmail());

        validateUniquePhoneNumber(phoneNumber);

        if (email != null) {
            validateUniqueEmail(email);
        }

        Contact contact = new Contact(
            request.getName().trim(),
            phoneNumber,
            email,
            normalize(request.getAddress())
        );

        Contact savedContact = contactRepository.save(contact);

        return convertToResponse(savedContact);
    }

    public ContactResponse updateContact(
            Long id,
            ContactRequest request) {

        Contact contact = findContactById(id);

        String phoneNumber = request.getPhoneNumber().trim();
        String email = normalize(request.getEmail());

        if (contactRepository.existsByPhoneNumberAndIdNot(
                phoneNumber,
                id)) {

            throw new DuplicateContactException(
                "Phone number already exists"
            );
        }

        if (email != null &&
            contactRepository.existsByEmailAndIdNot(email, id)) {

            throw new DuplicateContactException(
                "Email already exists"
            );
        }

        contact.setName(request.getName().trim());
        contact.setPhoneNumber(phoneNumber);
        contact.setEmail(email);
        contact.setAddress(normalize(request.getAddress()));

        Contact updatedContact =
            contactRepository.save(contact);

        return convertToResponse(updatedContact);
    }

    public void deleteContact(Long id) {

        Contact contact = findContactById(id);

        contactRepository.delete(contact);
    }

    private Contact findContactById(Long id) {

        return contactRepository
            .findById(id)
            .orElseThrow(() ->
                new ContactNotFoundException(
                    "Contact not found with id: " + id
                )
            );
    }

    private void validateUniquePhoneNumber(String phoneNumber) {

        if (contactRepository.existsByPhoneNumber(phoneNumber)) {

            throw new DuplicateContactException(
                "Phone number already exists"
            );
        }
    }

    private void validateUniqueEmail(String email) {

        if (contactRepository.existsByEmail(email)) {

            throw new DuplicateContactException(
                "Email already exists"
            );
        }
    }

    private ContactResponse convertToResponse(
            Contact contact) {

        return new ContactResponse(
            contact.getId(),
            contact.getName(),
            contact.getPhoneNumber(),
            contact.getEmail(),
            contact.getAddress(),
            contact.getCreatedAt()
        );
    }

    private String normalize(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
