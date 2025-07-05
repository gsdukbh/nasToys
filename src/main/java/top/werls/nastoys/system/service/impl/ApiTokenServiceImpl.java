package top.werls.nastoys.system.service.impl;

import org.springframework.stereotype.Service;
import top.werls.nastoys.common.utils.JwtTokenUtils;
import top.werls.nastoys.system.entity.ApiToken;
import top.werls.nastoys.system.repository.ApiTokenRepository;
import top.werls.nastoys.system.repository.SysUserRepository;
import top.werls.nastoys.system.service.ApiTokenService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import top.werls.nastoys.system.entity.SysUser;

@Service
public class ApiTokenServiceImpl implements ApiTokenService {

    private final ApiTokenRepository apiTokenRepository;

    private SysUserRepository sysUserRepository;

    private final JwtTokenUtils jwtTokenUtils;

    public ApiTokenServiceImpl(ApiTokenRepository apiTokenRepository, JwtTokenUtils jwtTokenUtils) {
        this.apiTokenRepository = apiTokenRepository;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    public ApiToken createToken(SysUser user, String name) {

        String tokenString = jwtTokenUtils.generateTokenWithoutExpiry(user.getUsername());
        ApiToken apiToken = new ApiToken();
        apiToken.setUid(user.getUid());
        apiToken.setName(name);
        apiToken.setToken(tokenString);
        return apiTokenRepository.save(apiToken);
    }

    @Override
    public void revokeToken(Long tokenId) {
        apiTokenRepository.findById(tokenId).ifPresent(token -> {
            apiTokenRepository.deleteById(tokenId);
        });
    }

    @Override
    public Optional<ApiToken> findByToken(String token) {
        Optional<ApiToken> apiTokenOptional = apiTokenRepository.findByToken(token);
        apiTokenOptional.ifPresent(apiToken -> {
            apiToken.setLastUsedAt(new Date());
            apiTokenRepository.save(apiToken);
        });
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

