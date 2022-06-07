package com.zzsong.iam.user.dto.args;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * 注册账号请求参数
 *
 * @author 宋志宗 on 2022/6/7
 */
@Getter
@Setter
public class RegisterArgs {

  /** 账号 */
  @Nullable
  private String account;

  /** 手机号 */
  @Nullable
  private String phone;

  /** 邮箱 */
  @Nullable
  private String email;

  /** 密码 */
  @Nullable
  private String password;

  /** 姓名 */
  @Nullable
  private String name;

  /** 昵称 */
  @Nullable
  private String nickname;

  /** 头像 */
  @Nullable
  private String profilePhoto;
}
