package com.zzsong.iam.infrastructure

import cn.idealframework.id.IDGenerator
import cn.idealframework.id.IDGeneratorFactory
import org.springframework.stereotype.Component

/**
 * @author 宋志宗 on 2022/6/7
 */
@Component
class IamIDGenerator(idGeneratorFactory: IDGeneratorFactory) : IDGenerator {
  private val idGenerator = idGeneratorFactory.getGenerator("zzsong.iam")

  override fun generate() = idGenerator.generate()
}
