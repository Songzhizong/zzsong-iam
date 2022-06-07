package com.zzsong.iam.user.configure

import com.zzsong.iam.user.infrastructure.password.encoder.IamPasswordEncoder
import com.zzsong.iam.user.infrastructure.password.encoder.PasswordEncoder
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing

/**
 * @author 宋志宗 on 2022/6/7
 */
@EnableReactiveMongoAuditing
@ComponentScan("com.zzsong.iam.user")
@EntityScan("com.zzsong.iam.user.domain.model")
@EnableConfigurationProperties(IamUserProperties::class)
class IamUserAutoConfigure {

  @Bean("iamPasswordEncoder")
  fun passwordEncoder(): PasswordEncoder = IamPasswordEncoder

}
