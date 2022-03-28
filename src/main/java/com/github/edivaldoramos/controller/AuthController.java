package com.github.edivaldoramos.controller;

import com.github.edivaldoramos.config.jwt.JwtUtils;
import com.github.edivaldoramos.model.response.AuthResponse;
import com.github.edivaldoramos.model.request.LoginRequest;
import com.github.edivaldoramos.model.response.MessageResponse;
import com.github.edivaldoramos.model.Role;
import com.github.edivaldoramos.model.RoleEnum;
import com.github.edivaldoramos.model.request.SignupRequest;
import com.github.edivaldoramos.model.User;
import com.github.edivaldoramos.model.UserDetailsImpl;
import com.github.edivaldoramos.repository.RoleRepository;
import com.github.edivaldoramos.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final String                DEFAULT_MSG_ROLE_NOT_FOUND = "Error: Role is not found.";
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtils        jwtUtils;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    return ResponseEntity.ok(
        new AuthResponse(
            jwt,
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail(),
            roles
        )
    );
  }
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
    if (userRepository.existsByUsername(signupRequest.getUsername())) {
      return ResponseEntity.
          badRequest()
          .body(new MessageResponse("Error: Username is already taken!"));
    }
    if (userRepository.existsByEmail(signupRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Email is already in use!"));
    }

    User user = new User(signupRequest.getUsername(),
        signupRequest.getEmail(),
        passwordEncoder.encode(signupRequest.getPassword())
    );

    Set<String> strRoles = signupRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
          .orElseThrow(() -> new RuntimeException(DEFAULT_MSG_ROLE_NOT_FOUND));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
          case "admin":
            Role adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException(DEFAULT_MSG_ROLE_NOT_FOUND));
            roles.add(adminRole);

            break;
          case "mod":
            Role modRole = roleRepository.findByName(RoleEnum.ROLE_MODERATOR)
                .orElseThrow(() -> new RuntimeException(DEFAULT_MSG_ROLE_NOT_FOUND));
            roles.add(modRole);

            break;
          default:
            Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new RuntimeException(DEFAULT_MSG_ROLE_NOT_FOUND));
            roles.add(userRole);
        }
      });
    }
    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
}