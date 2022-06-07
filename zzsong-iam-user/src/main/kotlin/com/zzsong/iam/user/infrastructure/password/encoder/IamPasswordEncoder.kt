package com.zzsong.iam.user.infrastructure.password.encoder

import cn.idealframework.crypto.HmacSHA1
import cn.idealframework.crypto.MD5
import cn.idealframework.crypto.SHA256
import cn.idealframework.lang.StringUtils
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * @author 宋志宗 on 2021/11/20
 */
object IamPasswordEncoder : PasswordEncoder {
  private const val SALT = "7aRw.fXAF*ohWgdJRH!aZyE7LEhWZ6Nz"

  override fun encode(rawPassword: CharSequence): String {
    return encryptPwd(rawPassword)
  }

  override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean {
    return encryptPwd(rawPassword.toString()) == encodedPassword
  }

  private fun encryptPwd(rawPassword: CharSequence): String {
    val rawPasswordStr = rawPassword.toString()
    val base64Encoder = Base64.getEncoder()
    val en1 = SHA256.encode(rawPasswordStr + SALT)
    val en2 = base64Encoder.encodeToString(HmacSHA1.encode(SALT, rawPasswordStr))
    val en3 = MD5.encode(en1 + SALT + en2 + rawPasswordStr)
    val en3Base64 = base64Encoder.encodeToString(en3.toByteArray(StandardCharsets.US_ASCII))
    val en1Base64 = base64Encoder.encodeToString(en1.toByteArray(StandardCharsets.US_ASCII))
    val s = "$2a$$en3Base64$en1Base64$en2"
    return StringUtils.replace(s, "=", "")
  }
}
