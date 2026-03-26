package com.example.demo.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.LoginRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        user.setEmail(user.getEmail().toLowerCase(Locale.ROOT));
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    public User login(LoginRequest loginRequest) {
        return userRepository.findByEmailAndPassword(
                        loginRequest.getEmail().toLowerCase(Locale.ROOT),
                        loginRequest.getPassword())
                .orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
