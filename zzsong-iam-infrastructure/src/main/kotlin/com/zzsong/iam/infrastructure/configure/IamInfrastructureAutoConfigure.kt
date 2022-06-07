package com.zzsong.iam.infrastructure.configure

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

/**
 * @author 宋志宗 on 2022/6/7
 */
@ComponentScan("com.zzsong.iam.infrastructure")
@EnableConfigurationProperties(IamInfrastructureProperties::class)
class IamInfrastructureAutoConfigure {
  
}
