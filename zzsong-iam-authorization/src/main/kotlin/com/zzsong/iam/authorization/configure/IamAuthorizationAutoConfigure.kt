package com.zzsong.iam.authorization.configure

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing

/**
 * @author 宋志宗 on 2022/6/7
 */
@EnableReactiveMongoAuditing
@ComponentScan("com.zzsong.iam.authorization")
@EntityScan("com.zzsong.iam.authorization.domain.model")
@EnableConfigurationProperties(IamAuthorizationProperties::class)
class IamAuthorizationAutoConfigure {
  
}
