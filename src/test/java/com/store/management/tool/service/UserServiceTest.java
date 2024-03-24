package com.store.management.tool.service;

import com.store.management.tool.domain.User;
import com.store.management.tool.domain.UserRole;
import com.store.management.tool.exception.DuplicateResourceException;
import com.store.management.tool.exception.ResourceNotFoundException;
import com.store.management.tool.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void testAddUser_Success() {
        User user = anUser();

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User addedUser = userService.add(user);

        assertNotNull(addedUser);
        assertEquals(user.getEmail(), addedUser.getEmail());
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testAddUser_DuplicateEmail() {
        User user = anUser();

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.add(user));

        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Collections.singletonList(anUser());

        when(userRepository.findAll()).thenReturn(users);

        List<User> retrievedUsers = userService.getAll();

        assertEquals(users.size(), retrievedUsers.size());
        assertEquals(users.get(0).getEmail(), retrievedUsers.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById_Success() throws ResourceNotFoundException {
        long userId = 1L;
        User user = anUser();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User retrievedUser = userService.getById(userId);

        assertNotNull(retrievedUser);
        assertEquals(user.getEmail(), retrievedUser.getEmail());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_NotFound() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getById(userId));

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testUpdateUser_Success() throws ResourceNotFoundException {
        long userId = 1L;
        User existingUser = anUser();
        User updatedUserDetails = anUser();
        updatedUserDetails.setPhoneNumber("0987654321");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.update(userId, updatedUserDetails);

        assertNotNull(updatedUser);
        assertEquals(updatedUserDetails.getEmail(), updatedUser.getEmail());
        assertEquals(updatedUserDetails.getPhoneNumber(), updatedUser.getPhoneNumber());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_NotFound() {
        long userId = 1L;
        User updatedUserDetails = anUser();

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.update(userId, updatedUserDetails));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        long userId = 1L;
        User existingUser = anUser();

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(existingUser));

        assertDoesNotThrow(() -> userService.delete(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(existingUser);
    }

    @Test
    void testDeleteUser_NotFound() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.delete(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).delete(any(User.class));
    }

    private User anUser() {
        User user = new User();

        user.setId(1L);
        user.setFirstName("Bill");
        user.setLastName("Gates");
        user.setEmail("bill@example.com");
        user.setPassword("123");
        user.setPhoneNumber("1234567890");
        user.setUserRole(UserRole.ADMIN);

        return user;
    }
}
