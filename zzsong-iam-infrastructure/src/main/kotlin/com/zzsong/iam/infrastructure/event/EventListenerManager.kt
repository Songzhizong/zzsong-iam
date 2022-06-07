package com.zzsong.iam.infrastructure.event

import cn.idealframework.event.message.EventMessage
import kotlinx.coroutines.CoroutineScope

/**
 * 事件监听器管理器
 *
 * @author 宋志宗 on 2022/4/6
 */
interface EventListenerManager {

  /**
   * 监听事件
   *
   * @param queueName  监听器名称
   * @param topic 事件主题
   * @param clazz 事件类型
   * @param block 处理逻辑
   * @author 宋志宗 on 2022/4/8
   */
  fun <T> listen(
    queueName: String,
    topic: String,
    clazz: Class<T>,
    block: suspend CoroutineScope.(EventMessage<T>) -> Unit
  ): EventListener
}
