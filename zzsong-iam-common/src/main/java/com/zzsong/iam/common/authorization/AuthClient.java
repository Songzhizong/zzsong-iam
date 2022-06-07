package com.zzsong.iam.common.authorization;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author 宋志宗 on 2022/2/23
 */
@Getter
@Setter
public class AuthClient {

  private long id;

  private String name;

  private String clientId;

  private long accessTokenValidity;

  private long refreshTokenValidity;

  private boolean accessTokenAutoRenewal;

  private boolean acceptRepetitionLogin;

  private String tokenValue;

  private boolean enabled;

  private LocalDateTime createdTime;

  private LocalDateTime updatedTime;
}
