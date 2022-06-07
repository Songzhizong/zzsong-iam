package com.zzsong.iam.user.infrastructure.password.encoder

import cn.idealframework.crypto.BCrypt

/**
 * @author 宋志宗 on 2021/7/5
 */
object BCryptPasswordEncoder : PasswordEncoder {

  override fun encode(rawPassword: CharSequence): String {
    val salt = BCrypt.gensalt()
    return BCrypt.hashpw(rawPassword.toString(), salt)
  }

  override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean {
    return BCrypt.checkpw(rawPassword.toString(), encodedPassword)
  }
}
