package com.zzsong.iam.common.user;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 审计用户信息
 *
 * @author 宋志宗 on 2022/6/7
 */
@Getter
@Setter
public class AuditUser {
  /** 用户id */
  @Nonnull
  private String userId;

  /** 用户姓名 */
  @Nullable
  private String username;
}
