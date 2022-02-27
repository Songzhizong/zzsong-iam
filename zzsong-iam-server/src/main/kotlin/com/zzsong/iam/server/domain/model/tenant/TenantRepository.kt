package com.zzsong.iam.server.domain.model.tenant

/**
 * @author 宋志宗 on 2022/2/26
 */
interface TenantRepository {

  /** 保存 */
  suspend fun save(tenantDo: TenantDo): TenantDo

  /** 删除 */
  suspend fun delete(tenantDo: TenantDo)

  /** 主键查询 */
  suspend fun findById(id: Long): TenantDo?

  /** 主键列表批量查询 */
  suspend fun findAllById(ids: Iterable<Long>): List<TenantDo>

  /** 通过路由模糊查询 */
  suspend fun findAllByRouterLike(routerLike: String): List<TenantDo>
}
