package com.garbage.booking.service;

import com.garbage.booking.model.User;
import com.garbage.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private String normalizeAndValidatePhone(String phone) {
        String normalized = phone == null ? "" : phone.replaceAll("\\D", "");
        if (!normalized.matches("^\\d{10}$")) {
            throw new RuntimeException("Phone number must be exactly 10 digits");
        }
        return normalized;
    }

    public User registerUser(User user) {
        String normalizedPhone = normalizeAndValidatePhone(user.getPhone());
        user.setPhone(normalizedPhone);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered: " + user.getEmail());
        }
        if (userRepository.existsByPhone(normalizedPhone)) {
            throw new RuntimeException("Phone number already registered: " + normalizedPhone);
        }
        // In production, encode password with BCrypt
        return userRepository.save(user);
    }

    public Optional<User> loginUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user;
        }
        return Optional.empty();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        String normalizedPhone = normalizeAndValidatePhone(updatedUser.getPhone());
        if (!normalizedPhone.equals(user.getPhone()) && userRepository.existsByPhone(normalizedPhone)) {
            throw new RuntimeException("Phone number already registered: " + normalizedPhone);
        }

        user.setName(updatedUser.getName());
        user.setPhone(normalizedPhone);
        user.setAddress(updatedUser.getAddress());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
