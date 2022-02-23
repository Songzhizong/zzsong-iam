package com.zzsong.iam.server.domain.model.auth;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * 认证身份信息
 *
 * @author 宋志宗 on 2022/2/23
 */
@Getter
@Setter
public class Authentication {

  /** 客户端ID */
  @Nonnull
  private String clientId;

  /** 用户唯一id */
  private long userId;

  @Nonnull
  public static Authentication create(long userId, @NotNull String clientId) {
    Authentication authentication = new Authentication();
    authentication.setClientId(clientId);
    authentication.setUserId(userId);
    return authentication;
  }
}
