package com.zzsong.iam.server.domain.model.auth;

import cn.idealframework.transmission.exception.UnauthorizedException;
import cn.idealframework.util.Asserts;
import com.zzsong.iam.server.infrastructure.encoder.password.PasswordEncoder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * 授权客户端
 * <pre>
 *  <b>indexes:</b>
 *  NORMAL:
 *    - name
 *  UNIQUE
 *    - clientId
 * </pre>
 *
 * @author 宋志宗 on 2022/2/23
 */
@Getter
@Setter
@CommonsLog
@Table("iam_oauth_client")
public class AuthClientDo {
  private static final long defaultAccessTokenValidity = Duration.ofMinutes(60).getSeconds();
  private static final long minAccessTokenValidity = Duration.ofMinutes(10).getSeconds();
  private static final long defaultRefreshTokenValidity = Duration.ofDays(15).getSeconds();
  private static final long minRefreshTokenValidity = Duration.ofHours(1).getSeconds();

  /** 主键 */
  @Id
  private long id = -1;

  /** 名称 */
  @Nonnull
  private String name = "";

  /** 客户端id */
  @Nonnull
  private String clientId = "";

  /** 客户端密码 */
  @Nonnull
  private String clientSecret = "";

  /** access token 有效期 */
  private long accessTokenValidity;

  /** refresh token 有效期 */
  private long refreshTokenValidity;

  /** 是否自动刷新accessToken */
  private boolean accessTokenAutoRenewal = true;

  /** 是否允许多设备同时登录 */
  private boolean acceptRepetitionLogin = false;

  @Nonnull
  private String tokenValue = "";

  /** 是否启用 */
  private boolean enabled = true;

  /** 乐观锁版本 */
  @Version
  private long version = 0;

  /** 创建时间 */
  @CreatedDate
  private LocalDateTime createdTime;

  /** 更新时间 */
  @LastModifiedDate
  private LocalDateTime updatedTime;

  @Nonnull
  public static AuthClientDo create(@Nonnull String name,
                                    @Nonnull String clientId,
                                    @Nonnull String clientSecret,
                                    @Nullable Long accessTokenValidity,
                                    @Nullable Long refreshTokenValidity,
                                    boolean accessTokenAutoRenewal,
                                    boolean acceptRepetitionLogin,
                                    @Nonnull PasswordEncoder passwordEncoder) {
    Asserts.notBlank(clientId, "客户端id不能为空");
    Asserts.notBlank(clientSecret, "客户端密码不能为空");
    AuthClientDo authClientDo = new AuthClientDo();
    authClientDo.setName(name);
    authClientDo.setClientId(clientId);
    authClientDo.setClientSecret(passwordEncoder.encode(clientSecret));
    authClientDo.setAccessTokenValidity(accessTokenValidity);
    authClientDo.setRefreshTokenValidity(refreshTokenValidity);
    authClientDo.setAccessTokenAutoRenewal(accessTokenAutoRenewal);
    authClientDo.setAcceptRepetitionLogin(acceptRepetitionLogin);
    String raw = clientId + ":" + clientSecret;
    byte[] bytes = raw.getBytes(StandardCharsets.UTF_8);
    String base64Value = Base64.getEncoder().encodeToString(bytes);
    authClientDo.setTokenValue("Basic " + base64Value);
    authClientDo.setEnabled(true);
    return authClientDo;
  }

  public void authenticate(@Nonnull String rawClientSecret,
                           @Nonnull PasswordEncoder passwordEncoder) {
    String clientId = getClientId();
    if (!getEnabled()) {
      log.info("客户端已被停用: " + clientId);
      throw new UnauthorizedException("无效的客户端");
    }
    String clientSecret = getClientSecret();
    boolean matches = passwordEncoder.matches(rawClientSecret, clientSecret);
    if (!matches) {
      log.info("客户端验证失败, 密码错误: " + clientId);
      throw new UnauthorizedException("客户端验证失败");
    }
  }

  @Nonnull
  public AuthClient toAuthClient() {
    AuthClient authClient = new AuthClient();
    authClient.setId(getId());
    authClient.setName(getName());
    authClient.setClientId(getClientId());
    authClient.setAccessTokenValidity(getAccessTokenValidity());
    authClient.setRefreshTokenValidity(getRefreshTokenValidity());
    authClient.setAccessTokenAutoRenewal(getAccessTokenAutoRenewal());
    authClient.setAcceptRepetitionLogin(getAcceptRepetitionLogin());
    authClient.setTokenValue(getTokenValue());
    authClient.setEnabled(getEnabled());
    authClient.setCreatedTime(getCreatedTime());
    authClient.setUpdatedTime(getUpdatedTime());
    return authClient;
  }

  public void setAccessTokenValidity(@Nullable Long accessTokenValidity) {
    if (accessTokenValidity == null) {
      accessTokenValidity = defaultAccessTokenValidity;
    }
    if (accessTokenValidity < minAccessTokenValidity) {
      accessTokenValidity = minAccessTokenValidity;
    }
    this.accessTokenValidity = accessTokenValidity;
  }

  public void setRefreshTokenValidity(@Nullable Long refreshTokenValidity) {
    if (refreshTokenValidity == null) {
      refreshTokenValidity = defaultRefreshTokenValidity;
    }
    if (refreshTokenValidity > minRefreshTokenValidity) {
      refreshTokenValidity = minRefreshTokenValidity;
    }
    this.refreshTokenValidity = refreshTokenValidity;
  }
}
