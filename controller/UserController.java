package com.example.demo.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.user.UserRequest;
import com.example.demo.dto.user.UserResponse;
import com.example.demo.dto.user.UserResponsePagination;
import com.example.demo.model.user.enumerated.Role;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','TELLER', 'BANK_MANAGER')")
    public ResponseEntity<UserResponse> me(
        Authentication authentication
    ) {
        UserResponse response = userService.getByEmail(authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("user/{email}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> updateEmail(
        @PathVariable String email,
        @RequestBody UserRequest request
    ) {
        userService.updateEmail(email, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/allUsers")
    @PreAuthorize("hasAnyRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<UserResponsePagination> getAllUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String role
    ) {
        UserResponsePagination pagination = userService.getUsers(page, size, role);
        return ResponseEntity.ok(pagination);
    }

    @GetMapping("/{id}")
    @PreAuthorize("HasAnyRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<UserResponse> getById(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping("/{email}")
    @PreAuthorize("hasAnyRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<UserResponse> getByEmail(
        @PathVariable String email
    ) {
        return ResponseEntity.ok(userService.getByEmail(email));
    }

    @PutMapping("/attiva/{id}")
    @PreAuthorize("HasAnyRole('TELLER', 'ADMIN')")
    public ResponseEntity<Void> activateUser(
        @PathVariable Long id
    ) {
        userService.enableUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/disattiva/{id}")
    @PreAuthorize("HasAnyRole('TELLER', 'ADMIN')")
    public ResponseEntity<Void> deactivateUser(
        @PathVariable Long id
    ) {
        userService.disableUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("aggiorna/{id}/ruolo")
    @PreAuthorize("HasAnyRole('TELLER', 'ADMIN')")
    public ResponseEntity<Void> updateUserRole(
        @PathVariable Long id,
        @RequestParam Role role
    ) {
        userService.updateUserRole(role, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cancella/{id}")
    @PreAuthorize("hasAnyRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<Void> deleteUser (
        @PathVariable Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active/byLogin")
    @PreAuthorize("hasAnyRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<UserResponsePagination> getActive(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam LocalDateTime lastLogin
    ) {
        UserResponsePagination pagination = userService.activeUsersByLogin(page, size, lastLogin);
        return ResponseEntity.ok(pagination);
    }

    @GetMapping("/active/byEnabled")
    @PreAuthorize("hasAnyRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<UserResponsePagination> getActiveByEnabled(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam boolean enabled
    ) {
        UserResponsePagination pagination = userService.activeUsersByEnabled(page, size, enabled);
        return ResponseEntity.ok(pagination);
    }

    @GetMapping("/newUsers")
    @PreAuthorize("hasAnyRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<UserResponsePagination> getNewUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam LocalDateTime from,
        @RequestParam LocalDateTime to
    ) {
        UserResponsePagination pagination = userService.newUsers(page, size, from, to);
        return ResponseEntity.ok(pagination);
    }
}
