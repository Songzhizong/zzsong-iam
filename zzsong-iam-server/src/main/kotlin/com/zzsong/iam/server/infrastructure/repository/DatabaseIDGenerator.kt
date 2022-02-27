package com.zzsong.iam.server.infrastructure.repository

import cn.idealframework.id.IDGenerator
import cn.idealframework.id.IDGeneratorFactory
import org.springframework.stereotype.Component

/**
 * 数据库主键生成器
 *
 * @author 宋志宗 on 2022/2/26
 */
@Component
class DatabaseIDGenerator(idGeneratorFactory: IDGeneratorFactory) : IDGenerator {
  private val idGenerator: IDGenerator = idGeneratorFactory.getGenerator("database")

  override fun generate(): Long {
    return idGenerator.generate()
  }
}
