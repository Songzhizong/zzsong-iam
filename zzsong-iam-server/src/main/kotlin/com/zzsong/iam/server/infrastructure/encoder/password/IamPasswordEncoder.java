package com.zzsong.iam.server.infrastructure.encoder.password;

import cn.idealframework.crypto.HmacSHA1;
import cn.idealframework.crypto.MD5;
import cn.idealframework.crypto.SHA256;
import cn.idealframework.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author 宋志宗 on 2021/11/20
 */
@Component
public class IamPasswordEncoder implements PasswordEncoder {
  private static final String PASSWORD_SALT = "u9*BUkjG*EFm4*M-x4LA7QnxdfK6PFdR";

  @Override
  public String encode(@Nonnull CharSequence rawPassword) {
    return encryptPwd(rawPassword);
  }

  @Override
  public boolean matches(@Nonnull CharSequence rawPassword, @Nonnull String encodedPassword) {
    return encryptPwd(rawPassword.toString()).equals(encodedPassword);
  }

  @Nonnull
  private String encryptPwd(@Nonnull CharSequence rawPassword) {
    String rawPasswordStr = rawPassword.toString();
    Base64.Encoder base64Encoder = Base64.getEncoder();
    String en1 = SHA256.encode(rawPasswordStr + PASSWORD_SALT);
    String en2 = base64Encoder.encodeToString(HmacSHA1.encode(PASSWORD_SALT, rawPasswordStr));
    String en3 = MD5.encode(en1 + PASSWORD_SALT + en2 + rawPasswordStr);
    String en3Base64 = base64Encoder.encodeToString(en3.getBytes(StandardCharsets.US_ASCII));
    String en1Base64 = base64Encoder.encodeToString(en1.getBytes(StandardCharsets.US_ASCII));
    String s = "$2a$" + en3Base64 + en1Base64 + en2;
    return StringUtils.replace(s, "=", "");
  }
}
