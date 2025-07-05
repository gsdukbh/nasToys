package top.werls.nastoys.system.service;

import top.werls.nastoys.system.entity.ApiToken;


import java.util.List;
import java.util.Optional;
import top.werls.nastoys.system.entity.SysUser;

public interface ApiTokenService {
    ApiToken createToken(SysUser user, String name);
    void revokeToken(Long tokenId);
    Optional<ApiToken> findByToken(String token);
    List<ApiToken> getTokensForUser(Long userId);
    Optional<ApiToken> getTokenById(Long tokenId);
}

