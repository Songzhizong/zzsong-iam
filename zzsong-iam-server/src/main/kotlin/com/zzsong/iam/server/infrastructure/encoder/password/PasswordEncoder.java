package com.zzsong.iam.server.infrastructure.encoder.password;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2021/7/5
 */
public interface PasswordEncoder {

  String encode(@Nonnull CharSequence rawPassword);

  boolean matches(@Nonnull CharSequence rawPassword, @Nonnull String encodedPassword);
}
