package com.zzsong.iam.user.infrastructure.password.encoder

import javax.annotation.Nonnull

/**
 * @author 宋志宗 on 2021/7/5
 */
interface PasswordEncoder {

  fun encode(rawPassword: CharSequence): String

  fun matches(rawPassword: CharSequence, @Nonnull encodedPassword: String): Boolean
}
