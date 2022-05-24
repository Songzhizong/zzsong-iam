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

  /** 所属平台 */
  private String platform;

  /** 客户端ID */
  @Nonnull
  private String clientId;

  /** 用户唯一id */
  private long userId;

  /** 用户姓名 */
  private String username;

  @Nonnull
  public static Authentication create(long userId,
                                      @Nonnull String username,
                                      @Nonnull String platform,
                                      @NotNull String clientId) {
    Authentication authentication = new Authentication();
    authentication.setPlatform(platform);
    authentication.setClientId(clientId);
    authentication.setUserId(userId);
    authentication.setUsername(username);
    return authentication;
  }
}
