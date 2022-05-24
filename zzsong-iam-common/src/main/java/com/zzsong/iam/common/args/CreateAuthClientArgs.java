package com.zzsong.iam.common.args;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/2/23
 */
@Getter
@Setter
public class CreateAuthClientArgs {

  /**
   * 客户端名称
   *
   * @required
   */
  @Nullable
  private String name;

  /**
   * 客户端唯一id
   *
   * @required
   */
  @Nullable
  private String clientId;

  /**
   * 客户端密码
   *
   * @required
   */
  @Nullable
  private String clientSecret;

  /** access token有效期 单位秒 */
  @Nullable
  private Long accessTokenValidity;

  /** refresh token有效期 单位秒 */
  @Nullable
  private Long refreshTokenValidity;

  /** token是否自动续期 */
  private boolean accessTokenAutoRenewal = true;

  /** 是否允许多设备登录 */
  private boolean acceptRepetitionLogin = false;
}
