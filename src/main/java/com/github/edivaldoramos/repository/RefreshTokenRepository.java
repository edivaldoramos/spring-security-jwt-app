package com.github.edivaldoramos.repository;

import com.github.edivaldoramos.model.RefreshToken;
import com.github.edivaldoramos.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);
  @Modifying
  int deleteByUser(User user);
}