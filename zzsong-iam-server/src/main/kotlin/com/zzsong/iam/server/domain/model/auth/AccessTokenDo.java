package com.zzsong.iam.server.domain.model.auth;

import cn.idealframework.date.DateTimes;
import cn.idealframework.json.JsonUtils;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.beans.Transient;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author 宋志宗 on 2022/2/23
 */
@Getter
@Setter
public class AccessTokenDo implements Serializable {
  private static final long serialVersionUID = -4654342306003703648L;
  public static String BEARER_TYPE = "Bearer";

  /** token值 */
  @Nonnull
  private String value;

  /** 是否自动续期 */
  private boolean autoRenewal;

  /** 计划有效时长, 单位秒 */
  private long validity;

  /** 过期时间 */
  private LocalDateTime expiration;

  @Nullable
  private RefreshTokenDo refreshToken;

  @Nonnull
  private Authentication authentication;

  @Nonnull
  public static AccessTokenDo create(boolean rememberMe,
                                     @Nonnull AuthClientDo authClientDo,
                                     @Nonnull Authentication authentication) {
    RefreshTokenDo refreshToken = null;
    if (rememberMe) {
      refreshToken = RefreshTokenDo
        .create(authentication, authClientDo.getRefreshTokenValidity());
    }
    return create(authClientDo, authentication, refreshToken);
  }

  @Nonnull
  public static AccessTokenDo create(@Nonnull AuthClientDo authClientDo,
                                     @Nonnull Authentication authentication,
                                     @Nullable RefreshTokenDo refreshToken) {
    AccessTokenDo accessTokenDo = new AccessTokenDo();
    accessTokenDo.setValue(UUID.randomUUID().toString().replace("-", ""));
    accessTokenDo.setAutoRenewal(authClientDo.getAccessTokenAutoRenewal());
    long accessTokenValidity = authClientDo.getAccessTokenValidity();
    accessTokenDo.setValidity(accessTokenValidity);
    accessTokenDo.setExpiration(LocalDateTime.now().plusSeconds(accessTokenValidity));
    accessTokenDo.setRefreshToken(refreshToken);
    accessTokenDo.setAuthentication(authentication);
    return accessTokenDo;
  }

  @Transient
  public boolean isExpired() {
    return expiration != null && DateTimes.now().isAfter(expiration);
  }

  @Transient
  public int getExpiresIn() {
    return expiration == null ? 0
      : (int) ((DateTimes.getTimestamp(expiration) - System.currentTimeMillis()) / 1000);
  }

  @Nonnull
  public AccessToken toAccessToken() {
    AccessToken accessToken = new AccessToken();
    accessToken.setAccess_token(this.getValue());
    accessToken.setToken_type(BEARER_TYPE);
    RefreshTokenDo refreshToken = this.getRefreshToken();
    if (refreshToken != null) {
      accessToken.setRefresh_token(refreshToken.getValue());
    }
    accessToken.setExpires_in(this.getExpiresIn());
    accessToken.setScope("all");
    return accessToken;
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this);
  }
}
