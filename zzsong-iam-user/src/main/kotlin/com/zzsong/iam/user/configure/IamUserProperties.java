package com.zzsong.iam.user.configure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 宋志宗 on 2022/6/7
 */
@Getter
@Setter
@ConfigurationProperties("iam.user")
public class IamUserProperties {

}
