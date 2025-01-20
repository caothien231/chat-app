package com.example.chat_app.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.chat_app.entities.User;
import com.example.chat_app.entities.UserTopic;
import com.example.chat_app.services.UserService;
import com.example.chat_app.services.UserTopicService;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;
    private final UserTopicService userTopicService;

    public UserController(UserService userService, UserTopicService userTopicService) {
        this.userService = userService;
        this.userTopicService = userTopicService;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()") // if authenticated, can go this
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")//only admin and super admin
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}/topics")
    public ResponseEntity<List<String>> getUserTopics(@PathVariable Integer userId) {
        List<String> topics = userTopicService.getUserTopics(userId);
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/friends")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can access their friends list
    public ResponseEntity<List<User>> getAllFriends() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        
        List<User> friends = userService.getFriends(currentUser.getId());
        
        return ResponseEntity.ok(friends);
    }

        // Add friend endpoint
    @PostMapping("/add-friend/{friendId}")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can add friends
    public ResponseEntity<String> addFriend(@PathVariable Integer friendId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        userService.addFriend(currentUser.getId(), friendId);

        return ResponseEntity.ok("Friend added successfully");
    }

    // Unfriend
    @PostMapping("/remove-friend/{friendId}")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can add friends
    public ResponseEntity<String> removeFriend(@PathVariable Integer friendId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        userService.removeFriend(currentUser.getId(), friendId);

        return ResponseEntity.ok("Friend removed successfully");
    }
}