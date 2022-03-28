package com.github.edivaldoramos.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<String> adminAccess() {
    return ResponseEntity.ok("Admin Board.");
  }

  @GetMapping("/all")
  public ResponseEntity<String> allAccess() {
    return ResponseEntity.ok("Public Content.");
  }

  @GetMapping("/mod")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<String> moderatorAccess() {
    return ResponseEntity.ok("Moderator Board.");
  }

  @GetMapping("/user")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<String> userAccess() {
    return ResponseEntity.ok("User Content.");
  }
}
