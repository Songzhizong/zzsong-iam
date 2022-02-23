package com.zzsong.iam.server.domain.model.auth;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 宋志宗 on 2022/2/23
 */
@Getter
@Setter
public class AccessToken {
  private String access_token;
  private String token_type;
  private String refresh_token;
  private int expires_in;
  private String scope;
}
