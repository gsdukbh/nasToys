package top.werls.nastoys.system.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.stereotype.Service;
import top.werls.nastoys.common.utils.JwtTokenUtils;
import top.werls.nastoys.system.entity.ApiToken;
import top.werls.nastoys.system.repository.ApiTokenRepository;
import top.werls.nastoys.system.service.ApiTokenService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import top.werls.nastoys.system.entity.SysUser;

@Service
public class ApiTokenServiceImpl implements ApiTokenService {

  private final ApiTokenRepository apiTokenRepository;


  private final JwtTokenUtils jwtTokenUtils;

  public ApiTokenServiceImpl(ApiTokenRepository apiTokenRepository,
      JwtTokenUtils jwtTokenUtils) {
    this.apiTokenRepository = apiTokenRepository;
    this.jwtTokenUtils = jwtTokenUtils;
  }

  @Override
  public ApiToken createToken(SysUser user, String name) {

    //        String tokenString = jwtTokenUtils.generateTokenWithoutExpiry(user.getUsername());

    String tokenString = JwtTokenUtils.generateApiToken("nastoys", 21);
    ApiToken apiToken = new ApiToken();
    apiToken.setUid(user.getUid());
    apiToken.setName(name);
    apiToken.setToken(JwtTokenUtils.hashString(tokenString));
    return apiTokenRepository.save(apiToken);
  }

  @Override
  public void revokeToken(Long tokenId) {
    apiTokenRepository.deleteById(tokenId);
  }

  @Override
  public Optional<ApiToken> findByToken(String token) {
    String hash = JwtTokenUtils.hashString(token);
    Optional<ApiToken> apiTokenOptional = apiTokenRepository.findByToken(hash);
    if (apiTokenOptional.isPresent()) {
      ApiToken apiToken = apiTokenOptional.get();
      if (apiToken.getExceededTime() != null && apiToken.getExceededTime().before(new Date())) {
        apiTokenOptional = Optional.empty(); // Token has exceeded its time limit
      }
      boolean isValid =
          MessageDigest.isEqual(
              apiToken.getToken().getBytes(StandardCharsets.UTF_8),
              hash.getBytes(StandardCharsets.UTF_8));
      if (isValid) {
        apiToken.setLastUsedAt(new Date());
        apiTokenRepository.save(apiToken);
      } else {
        apiTokenOptional = Optional.empty(); // Token does not match
      }
    }
    return apiTokenOptional;
  }

  @Override
  public List<ApiToken> getTokensForUser(Long userId) {
    return apiTokenRepository.findByUid(userId);
  }

  @Override
  public Optional<ApiToken> getTokenById(Long tokenId) {
    return apiTokenRepository.findById(tokenId);
  }

}
