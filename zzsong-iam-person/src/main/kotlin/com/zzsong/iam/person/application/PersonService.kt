package com.zzsong.iam.person.application

import com.zzsong.iam.person.domian.model.PersonRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * @author 宋志宗 on 2022/6/7
 */
@Service
class PersonService(private val personRepository: PersonRepository) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(PersonService::class.java)
  }


}
