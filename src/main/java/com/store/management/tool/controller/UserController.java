package com.store.management.tool.controller;

import com.store.management.tool.domain.User;
import com.store.management.tool.exception.ResourceNotFoundException;
import com.store.management.tool.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping()
    public ResponseEntity<User> add(@RequestBody User user) {
        User createdUser = userService.add(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdUser);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<User>> getAll() {
        List<User> users = userService.getAll();
        return ResponseEntity.status(users.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK)
                .body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        try {
            User user = userService.getById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.update(id, userDetails);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            userService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
