package com.zzsong.iam.infrastructure.event;

import cn.idealframework.boot.autoconfigure.event.IdealBootEventProperties;
import cn.idealframework.event.broker.rabbit.ReactiveRabbitEventPublisher;
import cn.idealframework.event.publisher.ReactiveEventPublisher;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.*;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/4/2
 */
@CommonsLog
@Configuration
@RequiredArgsConstructor
public class ReactiveEventAutoConfigure implements SmartInitializingSingleton {
  private final AmqpAdmin amqpAdmin;
  private final IdealBootEventProperties eventProperties;

  @Bean
  public Mono<Connection> connectionMono(@Nonnull EventRabbitProperties rabbitProperties) {
    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.setHost(rabbitProperties.getHost());
    connectionFactory.setPort(rabbitProperties.getPort());
    connectionFactory.setUsername(rabbitProperties.getUsername());
    connectionFactory.setPassword(rabbitProperties.getPassword());
    connectionFactory.setVirtualHost(rabbitProperties.getVirtualHost());
    return Mono.fromCallable(() -> connectionFactory.newConnection("cloudcare-monitor-rabbit")).cache();
  }

  @Bean
  public Sender sender(Mono<Connection> connectionMono) {
    return RabbitFlux.createSender(new SenderOptions().connectionMono(connectionMono));
  }

  @Bean
  public Receiver receiver(Mono<Connection> connectionMono) {
    return RabbitFlux.createReceiver(new ReceiverOptions().connectionMono(connectionMono));
  }

  @Bean
  public ReactiveEventPublisher reactiveEventPublisher(@Nonnull Sender sender) {
    String exchange = eventProperties.getBroker().getRabbit().getExchange();
    return new ReactiveRabbitEventPublisher(sender, exchange);
  }

  @Bean
  public TopicExchange eventExchange() {
    String exchange = eventProperties.getBroker().getRabbit().getExchange();
    return new TopicExchange(exchange);
  }

  @Override
  public void afterSingletonsInstantiated() {
    amqpAdmin.declareExchange(this.eventExchange());
  }
}
