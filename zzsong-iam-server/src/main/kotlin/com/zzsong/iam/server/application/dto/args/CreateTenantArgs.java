package com.zzsong.iam.server.application.dto.args;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/2/26
 */
@Getter
@Setter
public class CreateTenantArgs {
  /**
   * 租户名称
   *
   * @required
   */
  @Nullable
  private String name;

  /**
   * 管理员用户id
   *
   * @required
   */
  @Nullable
  private Long ownerUserId;

  /** 父租户id */
  @Nullable
  private Long pid;
}
