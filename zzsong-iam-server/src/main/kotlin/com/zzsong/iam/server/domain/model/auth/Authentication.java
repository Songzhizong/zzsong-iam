package com.zzsong.iam.server.domain.model.auth;

import lombok.Getter;
import lombok.Setter;

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
  @Nonnull
  private long userId;
}
