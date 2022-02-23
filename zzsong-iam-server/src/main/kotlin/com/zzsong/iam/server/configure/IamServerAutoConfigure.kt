package com.zzsong.iam.server.configure

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * @author 宋志宗 on 2022/1/26
 */
@Configuration
@EnableScheduling
@EnableR2dbcAuditing
@ComponentScan("com.zzsong.iam.server")
@EntityScan("com.zzsong.iam.server.domain.model")
@EnableR2dbcRepositories("com.zzsong.iam.server")
@EnableConfigurationProperties(IamServerProperties::class)
class IamServerAutoConfigure {

}
