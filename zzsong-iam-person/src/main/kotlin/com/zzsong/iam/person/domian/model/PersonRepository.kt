package com.zzsong.iam.person.domian.model

/**
 * @author 宋志宗 on 2022/6/7
 */
interface PersonRepository {

  suspend fun save(person: PersonDo): PersonDo

  suspend fun delete(person: PersonDo)

  suspend fun findById(id: Long): PersonDo?
}
