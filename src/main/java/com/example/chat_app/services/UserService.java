package com.example.chat_app.services;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.chat_app.dtos.RegisterUserDto;
import com.example.chat_app.entities.Role;
import com.example.chat_app.entities.RoleEnum;
import com.example.chat_app.entities.User;
import com.example.chat_app.repositories.RoleRepository;
import com.example.chat_app.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }

    public User createAdministrator(RegisterUserDto input) {
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.ADMIN);

        if (optionalRole.isEmpty()) {
            return null;
        }

        var user = new User();
        user.setFullName(input.getFullName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRole(optionalRole.get());

        return userRepository.save(user);
    }

    public List<User> getFriends(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new ArrayList<>(user.getFriends()); // Convert Set to List for return
    }

    public void addFriend(Integer userId, Integer friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        // Check if the friend relationship already exists
        if (!user.getFriends().contains(friend)) {
            user.getFriends().add(friend);          // Add friend to user's list
            friend.getFriends().add(user);          // Add user to friend's list for bidirectional relationship
            userRepository.save(user);              // Save user
            userRepository.save(friend);            // Save friend
        }
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        System.out.println("Check Remove " + userId + " and " + friendId);
        System.out.println("Current friends of user " + userId + ": " + user.getFriends());
        // Check if the friend relationship already exists
        if (user.getFriends().contains(friend)) {
            user.getFriends().remove(friend);
            friend.getFriends().remove(user);
            System.out.println("Removing friend relationship between user " + userId + " and " + friendId);
        }
    
        // Save both users to update the database
        userRepository.save(user);
        userRepository.save(friend);
    }
}