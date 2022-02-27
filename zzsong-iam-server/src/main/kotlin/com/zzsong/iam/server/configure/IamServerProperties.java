package com.zzsong.iam.server.configure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2021/11/18
 */
@Getter
@Setter
@ConfigurationProperties("iam.server")
public class IamServerProperties {

  /** 是否开启注册功能 */
  private boolean registerEnabled = true;

  /** token key 前缀 */
  @Nonnull
  private String cachePrefix = "iam";
}
