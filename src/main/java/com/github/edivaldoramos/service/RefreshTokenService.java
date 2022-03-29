package com.github.edivaldoramos.service;

import com.github.edivaldoramos.exception.TokenRefreshException;
import com.github.edivaldoramos.model.RefreshToken;
import com.github.edivaldoramos.repository.RefreshTokenRepository;
import com.github.edivaldoramos.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
  @Value("${app.refresh-expiration-ms}")
  private Long refreshTokenDurationMs;
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }
  public RefreshToken createRefreshToken(Long userId) {
    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUser(userRepository.findById(userId).get());
    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken = refreshTokenRepository.save(refreshToken);
    return refreshToken;
  }
  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
    }
    return token;
  }
  @Transactional
  public int deleteByUserId(Long userId) {
    return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
  }

}
