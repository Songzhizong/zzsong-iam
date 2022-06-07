package com.zzsong.iam.infrastructure.event;

import cn.idealframework.cache.ReactiveCache;
import cn.idealframework.cache.impl.ReactiveRedisCacheFactory;
import cn.idealframework.cache.serialize.StringDeserializer;
import cn.idealframework.cache.serialize.StringSerializer;
import cn.idealframework.event.message.EventMessage;
import cn.idealframework.event.message.EventSupplier;
import cn.idealframework.event.message.impl.GeneralEventMessage;
import cn.idealframework.event.publisher.ReactiveEventPublisher;
import cn.idealframework.event.publisher.ReactiveTransactionalEventPublisher;
import cn.idealframework.id.IDGenerator;
import cn.idealframework.id.IDGeneratorFactory;
import cn.idealframework.json.JsonUtils;
import cn.idealframework.lang.CollectionUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author 宋志宗 on 2022/4/1
 */
@Component
@CommonsLog
public class ReactiveMongoTemplateTransactionalEventPublisher implements ReactiveTransactionalEventPublisher, ApplicationRunner, DisposableBean {
  private static final String LOCK_VALUE = UUID.randomUUID().toString();
  private final IDGenerator idGenerator;
  private final ReactiveMongoTemplate reactiveMongoTemplate;
  private final ReactiveEventPublisher reactiveEventPublisher;
  private final ReactiveCache<String> lock;
  private final AtomicBoolean atomicBoolean = new AtomicBoolean(true);

  public ReactiveMongoTemplateTransactionalEventPublisher(@Nonnull ReactiveMongoTemplate reactiveMongoTemplate,
                                                          @Nonnull IDGeneratorFactory idGeneratorFactory,
                                                          @Nonnull ReactiveEventPublisher reactiveEventPublisher,
                                                          @Nonnull ReactiveRedisCacheFactory redisCacheFactory) {
    this.reactiveMongoTemplate = reactiveMongoTemplate;
    this.idGenerator = idGeneratorFactory.getGenerator("event.temp");
    this.reactiveEventPublisher = reactiveEventPublisher;
    this.lock = redisCacheFactory.<String>newBuilder()
      .serializer(StringSerializer.instance())
      .deserializer(StringDeserializer.instance())
      .expireAfterWrite(Duration.ofMinutes(5))
      .build("event.trans.mongo");
  }

  @Nonnull
  @Override
  public Mono<Boolean> publish(@Nonnull Collection<EventSupplier> suppliers) {
    if (CollectionUtils.isEmpty(suppliers)) {
      return Mono.just(true);
    }
    long currentTimeMillis = System.currentTimeMillis();
    List<EventTemp> collect = suppliers.stream().map(s -> {
      EventMessage<?> message = s.getEventMessage();
      String jsonString = JsonUtils.toJsonString(message);
      EventTemp temp = new EventTemp();
      temp.setId(idGenerator.generate());
      temp.setEventInfo(jsonString);
      temp.setTimestamp(currentTimeMillis);
      return temp;
    }).collect(Collectors.toList());
    return reactiveMongoTemplate.insert(collect, EventTemp.class).collectList().map(t -> true);
  }

  @Override
  public void run(ApplicationArguments args) {
    this.start();
  }

  private void start() {
    new Thread(() -> {
      while (atomicBoolean.get()) {
        boolean executed = false;
        try {
          Boolean tryLock = lock.putIfAbsent("lock", LOCK_VALUE, Duration.ofSeconds(60)).block();
          if (tryLock == null || !tryLock) {
            continue;
          }
          executed = true;
          Query query = new Query().limit(100)
            .with(Sort.by(Sort.Order.asc("id")));
          List<EventTemp> temps = reactiveMongoTemplate
            .findAllAndRemove(query, EventTemp.class).collectList().block();
          if (temps == null) {
            continue;
          }
          List<EventSupplier> collect = temps.stream().map(t -> JsonUtils.parse(t.getEventInfo(), GeneralEventMessage.class))
            .collect(Collectors.toList());
          CountDownLatch countDownLatch = new CountDownLatch(1);
          reactiveEventPublisher.publish(collect)
            .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(5)))
            .doFinally(f -> countDownLatch.countDown())
            .subscribe();
          countDownLatch.countDown();
        } catch (Exception e) {
          log.info("发布消息出现异常: ", e);
        } finally {
          try {
            if (executed) {
              lock.invalidateIfValue("lock", LOCK_VALUE).block();
            }
            TimeUnit.SECONDS.sleep(1);
          } catch (InterruptedException e) {
            // ignore
          }
        }
      }
    }).start();
  }

  @Override
  public void destroy() {
    atomicBoolean.set(false);
  }
}
