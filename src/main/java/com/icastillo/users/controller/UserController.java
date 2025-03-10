package com.icastillo.users.controller;

import java.util.List;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.icastillo.users.exception.*;
import com.icastillo.users.model.User;
import com.icastillo.users.model.Phone;
import com.icastillo.users.utils.JwtUtil;
import com.icastillo.users.repository.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Value("${validation.regex.password.regex}") 
    private String passwordRegex;

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new InvalidEmailException();
        }

        if (user.getPassword() == null || !user.getPassword().matches(passwordRegex)) {
            throw new InvalidPasswordException();
        }

        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setIsActive(true);

        if (user.getPhones() != null) {
            for (Phone phone : user.getPhones()) {
                phone.setUser(user);
            }
        }

        String token = JwtUtil.generateToken(user.getEmail());
        user.setToken(token);

        User savedUser = userRepository.save(user);

        return ResponseEntity.status(201).body(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (userDetails.getName() != null) {
            existingUser.setName(userDetails.getName());
        }
        if (userDetails.getEmail() != null) {
            existingUser.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPassword() != null) {
            if (!userDetails.getPassword().matches(passwordRegex)) {
                throw new InvalidPasswordException();
            }
            existingUser.setPassword(userDetails.getPassword());
        }

        if (userDetails.getPhones() != null) {
            existingUser.getPhones().clear();
            for (Phone phone : userDetails.getPhones()) {
                phone.setUser(existingUser);
                existingUser.getPhones().add(phone);
            }
        }

        existingUser.setModified(LocalDateTime.now());

        String token = JwtUtil.generateToken(existingUser.getEmail());
        existingUser.setToken(token);

        return ResponseEntity.ok(userRepository.save(existingUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable(value = "id") Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        userRepository.delete(user);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuario eliminado con éxito");
        return ResponseEntity.status(200).body(response);
    }
}
