package com.zzsong.iam.user.dto.args;

import cn.idealframework.transmission.Paging;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/6/7
 */
@Getter
@Setter
public class QueryUserArgs {
  @Nonnull
  private Paging paging = Paging.of(1, 10);

  /** 姓名 */
  @Nullable
  private String name;

  /** 账号 */
  @Nullable
  private String account;

  /** 手机号 */
  @Nullable
  private String phone;

  /** 邮箱 */
  @Nullable
  private String email;
}
