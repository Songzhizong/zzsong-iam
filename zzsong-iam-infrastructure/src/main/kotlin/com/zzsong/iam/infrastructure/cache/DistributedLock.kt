package com.zzsong.iam.infrastructure.cache

import cn.idealframework.cache.impl.ReactiveRedisCacheFactory
import cn.idealframework.cache.serialize.StringDeserializer
import cn.idealframework.cache.serialize.StringSerializer
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * @author 宋志宗 on 2022/4/5
 */
@Component
class DistributedLock(reactiveRedisCacheFactory: ReactiveRedisCacheFactory) {
  private val lock = reactiveRedisCacheFactory.newBuilder<String>()
    .serializer(StringSerializer.instance())
    .deserializer(StringDeserializer.instance())
    .build("lock")

  suspend fun tryLock(name: String, value: String, timeout: Duration): Boolean {
    return lock.putIfAbsent(name, value, timeout).awaitSingleOrNull() ?: false
  }

  suspend fun unlock(name: String, value: String) {
    lock.invalidateIfValue(name, value).awaitSingleOrNull()
  }
}
