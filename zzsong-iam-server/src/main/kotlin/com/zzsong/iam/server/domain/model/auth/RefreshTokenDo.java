package com.zzsong.iam.server.domain.model.auth;

import cn.idealframework.date.DateTimes;
import cn.idealframework.json.JsonUtils;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.beans.Transient;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author 宋志宗 on 2022/2/23
 */
@Getter
@Setter
public class RefreshTokenDo implements Serializable {
  private static final long serialVersionUID = 1374491653519779111L;

  private String value;

  /** 过期时间 */
  private LocalDateTime expiration;

  /** 用户身份信息 */
  @Nonnull
  private Authentication authentication;

  @Nonnull
  public static RefreshTokenDo create(@Nonnull Authentication authentication, long timeoutSeconds) {
    RefreshTokenDo refreshTokenDo = new RefreshTokenDo();
    refreshTokenDo.setValue(UUID.randomUUID().toString().replace("-", ""));
    refreshTokenDo.setExpiration(LocalDateTime.now().plusSeconds(timeoutSeconds));
    refreshTokenDo.setAuthentication(authentication);
    return refreshTokenDo;
  }

  @Transient
  public int getExpiresIn() {
    return expiration == null ? 0
      : (int) ((DateTimes.getTimestamp(expiration) - System.currentTimeMillis()) / 1000);
  }

  @Transient
  public boolean isExpired() {
    return expiration != null && DateTimes.now().isAfter(expiration);
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this);
  }
}
