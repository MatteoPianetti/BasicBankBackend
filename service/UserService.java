package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import com.example.demo.dto.user.UserRequest;
import com.example.demo.dto.user.UserResponse;
import com.example.demo.dto.user.UserResponsePagination;
import com.example.demo.exception.NoEntityFoundException;
import com.example.demo.model.user.User;
import com.example.demo.model.user.enumerated.Role;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void updateEmail(String newEmail, UserRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new NoEntityFoundException("Nessun utente trovato"));

        if(request.getEmail().equals(newEmail)) {
            throw new IllegalArgumentException("La mail nuova, è uguale a quella vecchia");
        }

        user.setEmail(newEmail);
        userRepository.save(user);
    }


    public void updatePhone(String newPhone, UserRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new NoEntityFoundException("Nessun utente trovato"));

        if(request.getPhone().equals(newPhone)) {
            throw new IllegalArgumentException("La mail nuova, è uguale a quella vecchia");
        }

        user.setEmail(newPhone);
        userRepository.save(user);
    }

    public UserResponsePagination getUsers(int page, int size, String role) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users;

        if(role != null) {
            Role r = Role.valueOf(role);
            users = userRepository.findByRole(r, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        List<User> listOfUsers = users.getContent();
        List<UserResponse> content = listOfUsers.stream().map(UserResponse::from).toList();

        UserResponsePagination pag = new UserResponsePagination();
        pag.setContent(content);
        pag.setPages(users.getNumber());
        pag.setPageSize(users.getSize());
        pag.setTotalElements(users.getTotalElements());
        pag.setTotalPages(users.getTotalPages());
        pag.setLast(users.isLast());

        return pag;
    }

    public UserResponse getById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoEntityFoundException("Nessuno user trovato"));
        
        return UserResponse.from(user);
    }

    public UserResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoEntityFoundException("Nessuno user trovato"));
        
        return UserResponse.from(user);
    }

    public void enableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoEntityFoundException("Nessun utente trovato"));

        user.setEnabled(true);
        userRepository.save(user);
    }

    public void disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoEntityFoundException("Nessun utente trovato"));

        user.setEnabled(false);
        userRepository.save(user);
    }

    public void updateUserRole(Role role, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoEntityFoundException("Nessun utente trovato"));
        if(user.getRole() == role) {
            throw new IllegalArgumentException("Non stai modificando il ruolo dell'utente");
        }

        user.setRole(role);
        userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoEntityFoundException("Nessun utente trovato"));
    
        userRepository.delete(user);
    }

    public UserResponsePagination activeUsersByLogin(int page, int size, LocalDateTime lastLogin) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findByLastLogin(lastLogin, pageable);
        List<User> listOfUsers = users.getContent();
        List<UserResponse> content = listOfUsers.stream().map(UserResponse::from).toList();

        UserResponsePagination pag = new UserResponsePagination();
        pag.setContent(content);
        pag.setPages(users.getNumber());
        pag.setPageSize(users.getSize());
        pag.setTotalElements(users.getTotalElements());
        pag.setTotalPages(users.getTotalPages());
        pag.setLast(users.isLast());

        return pag;
    }

    public UserResponsePagination activeUsersByEnabled(int page, int size, boolean enabled) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findByEnabled(enabled, pageable);
        List<User> listOfUsers = users.getContent();
        List<UserResponse> content = listOfUsers.stream().map(UserResponse::from).toList();

        UserResponsePagination pag = new UserResponsePagination();
        pag.setContent(content);
        pag.setPages(users.getNumber());
        pag.setPageSize(users.getSize());
        pag.setTotalElements(users.getTotalElements());
        pag.setTotalPages(users.getTotalPages());
        pag.setLast(users.isLast());

        return pag;
    }

    public UserResponsePagination newUsers(int page, int size, LocalDateTime from, LocalDateTime to) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findNewUsers(from, to, pageable);
        List<User> listOfUsers = users.getContent();
        List<UserResponse> content = listOfUsers.stream().map(UserResponse::from).toList();

        UserResponsePagination pag = new UserResponsePagination();
        pag.setContent(content);
        pag.setPages(users.getNumber());
        pag.setPageSize(users.getSize());
        pag.setTotalElements(users.getTotalElements());
        pag.setTotalPages(users.getTotalPages());
        pag.setLast(users.isLast());

        return pag;
    }
}
