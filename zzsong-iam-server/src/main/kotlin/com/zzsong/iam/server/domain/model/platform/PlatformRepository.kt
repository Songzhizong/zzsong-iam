package com.zzsong.iam.server.domain.model.platform

/**
 * @author 宋志宗 on 2022/4/1
 */
interface PlatformRepository {

  /** 保存 */
  suspend fun save(platformDo: PlatformDo): PlatformDo

  /** 通过平台编码查询 */
  suspend fun findByCode(code: String): PlatformDo?
}
