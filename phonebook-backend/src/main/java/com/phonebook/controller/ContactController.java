package com.phonebook.controller;

import com.phonebook.dto.ContactRequest;
import com.phonebook.dto.ContactResponse;
import com.phonebook.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
@CrossOrigin(origins = "http://localhost:5173")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // GET /contacts
    @GetMapping
    public List<ContactResponse> getAllContacts() {

        return contactService.getAllContacts();
    }

    // GET /contacts/search?query=&page=0&size=10
    @GetMapping("/search")
    public Page<ContactResponse> searchContacts(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        Pageable pageable = PageRequest.of(
            safePage,
            safeSize,
            Sort.by(Sort.Direction.ASC, "name")
        );

        return contactService.searchContacts(query, pageable);
    }

    // GET /contacts/{id}
    @GetMapping("/{id}")
    public ContactResponse getContactById(
            @PathVariable Long id) {

        return contactService.getContactById(id);
    }

    // POST /contacts
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactResponse createContact(
            @Valid @RequestBody ContactRequest request) {

        return contactService.createContact(request);
    }

    // PUT /contacts/{id}
    @PutMapping("/{id}")
    public ContactResponse updateContact(
            @PathVariable Long id,
            @Valid @RequestBody ContactRequest request) {

        return contactService.updateContact(id, request);
    }

    // DELETE /contacts/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContact(
            @PathVariable Long id) {

        contactService.deleteContact(id);
    }
}
