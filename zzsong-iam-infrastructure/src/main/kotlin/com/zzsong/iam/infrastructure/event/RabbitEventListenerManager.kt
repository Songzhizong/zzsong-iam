package com.zzsong.iam.infrastructure.event

import cn.idealframework.boot.autoconfigure.event.IdealBootEventProperties
import cn.idealframework.cache.ReactiveCache
import cn.idealframework.cache.impl.ReactiveRedisCacheFactory
import cn.idealframework.cache.serialize.StringDeserializer
import cn.idealframework.cache.serialize.StringSerializer
import cn.idealframework.event.message.EventMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.boot.ApplicationArguments
import org.springframework.stereotype.Component
import reactor.core.Disposable
import reactor.rabbitmq.ConsumeOptions
import reactor.rabbitmq.Receiver
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap


/**
 * 事件监听器管理器
 *
 * @author 宋志宗 on 2022/4/2
 */
@Component("iamRabbitEventListenerManager")
class RabbitEventListenerManager(
  private val receiver: Receiver,
  private val amqpAdmin: AmqpAdmin,
  private val eventExchange: TopicExchange,
  redisCacheFactory: ReactiveRedisCacheFactory,
  private val idealBootEventProperties: IdealBootEventProperties
) : EventListenerManager {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(RabbitEventListener::class.java)
    private val registry = ConcurrentHashMap<String, RabbitEventListener<*>>()
    private val timeout = Duration.ofMinutes(10)
  }

  private val cache: ReactiveCache<String> = redisCacheFactory.newBuilder<String>()
    .serializer(StringSerializer.instance())
    .deserializer(StringDeserializer.instance())
    .expireAfterWrite(timeout)
    .build("event.idempotent")


  @Suppress("UNCHECKED_CAST")
  override fun <T> listen(
    queueName: String,
    topic: String,
    clazz: Class<T>,
    block: suspend CoroutineScope.(EventMessage<T>) -> Unit
  ): RabbitEventListener<T> {
    var exist = true
    val eventListener = registry.computeIfAbsent(queueName) {
      log.info("注册事件监听器: {}  ->  {}", queueName, topic)
      exist = false
      RabbitEventListener(
        receiver,
        amqpAdmin,
        eventExchange,
        idealBootEventProperties,
        queueName,
        topic,
        clazz,
        cache,
        block
      )
    } as RabbitEventListener<T>
    if (exist) {
      val message = "监听器名称: $queueName 被重复注册"
      log.error(message)
      throw RuntimeException(message)
    }
    return eventListener
  }

  class RabbitEventListener<T>(
    private val receiver: Receiver,
    amqpAdmin: AmqpAdmin,
    eventExchange: TopicExchange,
    idealBootEventProperties: IdealBootEventProperties,
    queueName: String,
    topic: String,
    private val clazz: Class<T>,
    private val cache: ReactiveCache<String>,
    private val block: suspend CoroutineScope.(EventMessage<T>) -> Unit
  ) : EventListener {
    private val lockValue = UUID.randomUUID().toString()
    private val finalQueueName: String
    private var disposable: Disposable? = null


    init {
      val rabbit = idealBootEventProperties.broker.rabbit
      val enableLocalModel = rabbit.isEnableLocalModel
      val queuePrefix = rabbit.queuePrefix
      val queue = if (enableLocalModel) {
        finalQueueName =
          queuePrefix + "." + queueName + "." + UUID.randomUUID().toString().replace("-", "")
        Queue(finalQueueName, false, false, true)
      } else {
        finalQueueName = "$queuePrefix.$queueName"
        Queue(finalQueueName, true, false, false)
      }
      amqpAdmin.declareQueue(queue)
      amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(eventExchange).with(topic))
    }

    private fun start() {
      val options = ConsumeOptions()
      disposable = receiver.consumeManualAck(finalQueueName, options)
        .flatMap { delivery ->
          mono {
            var ack = true
            try {
              val body = delivery.body
              val string = String(body, Charsets.UTF_8)
              val message = try {
                EventMessage.parse(string, clazz)
              } catch (e: Exception) {
                log.info("反序列化事件消息出现异常 {} ", clazz.name, e)
                return@mono
              }
              val uuid = message.uuid()
              val key = "$finalQueueName:$uuid"
              val tryLock =
                cache.putIfAbsent(key, lockValue, timeout).awaitSingleOrNull()
              try {
                if (tryLock == true) {
                  block.invoke(this, message)
                }
              } catch (e: Exception) {
                ack = false
                try {
                  if (uuid.isNotBlank()) {
                    cache.invalidateIfValue(key, lockValue).awaitSingleOrNull()
                  }
                  log.warn("处理出现异常: ", e)
                  delay(1000)
                } catch (e: Exception) {
                  log.info("异常的后续处理出现异常: ", e)
                }
              }
            } finally {
              if (ack) {
                delivery.ack()
              } else {
                delivery.nack(true)
              }
            }
          }
        }.subscribe()
    }

    private fun stop() {
      if (disposable?.isDisposed == true) {
        return
      }
      disposable?.dispose()

    }

    override fun destroy() {
      this.stop()
    }

    override fun run(args: ApplicationArguments?) {
      this.start()
    }
  }
}
