package com.store.management.tool.service;

import com.store.management.tool.domain.User;
import com.store.management.tool.exception.DuplicateResourceException;
import com.store.management.tool.exception.ResourceNotFoundException;
import com.store.management.tool.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public User add(User user)  {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("User with email '" + user.getEmail() + "' already exists.");
        }
        var encodedPassword = this.encodedPassword(user.getPassword());
        user.setPassword(encodedPassword);
        User newUser = userRepository.save(user);
        log.info("Successfully added new user {} with email {}.", newUser.getId(), newUser.getEmail());
        return newUser;
    }

    public List<User> getAll() {
        log.info("Retrieving all users.");
        return userRepository.findAll();
    }

    public User getById(Long id) throws ResourceNotFoundException {
        log.info("Retrieving user by id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id: %d not found", id)));
    }

    public User update(Long id, User userDetails) throws ResourceNotFoundException {
        log.info("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id: %d not found", id)));

        user.setEmail(userDetails.getEmail());
        var passwordEncoded = this.encodedPassword(userDetails.getPassword());
        user.setPassword(passwordEncoded);
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPhoneNumber(userDetails.getPhoneNumber());

        return userRepository.save(user);
    }

    public void delete(Long id) throws ResourceNotFoundException {
        log.info("Deleting user with id: {}", id);
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id: %d not found", id)));

        userRepository.delete(userToDelete);
        log.info("User {} deleted successfully", id);
    }

    private String encodedPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
}
