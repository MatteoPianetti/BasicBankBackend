package com.example.demo.dto.user;

import java.time.LocalDateTime;

import com.example.demo.model.user.User;
import com.example.demo.model.user.enumerated.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserResponse {
    private Long id;
    private String phone;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Role role;
    private LocalDateTime createdAt;
    private boolean enabled;

    public static UserResponse from(User u) {
        UserResponse res = new UserResponse();

        res.setId(u.getId());
        res.setPhone(u.getPhone());
        res.setEmail(u.getEmail());
        res.setPassword(u.getPassword());
        res.setFirstName(u.getFirstName());
        res.setLastName(u.getLastName());
        res.setRole(u.getRole());
        res.setCreatedAt(u.getCreatedAt());
        res.setEnabled(u.getEnabled());

        return res;
    }
}
