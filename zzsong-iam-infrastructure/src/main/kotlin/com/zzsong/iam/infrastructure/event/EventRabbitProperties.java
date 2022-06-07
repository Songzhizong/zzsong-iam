package com.zzsong.iam.infrastructure.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 宋志宗 on 2022/4/2
 */
@Getter
@Setter
@Component
@ConfigurationProperties("spring.rabbitmq")
public class EventRabbitProperties {

  /**
   * RabbitMQ host. Ignored if an address is set.
   */
  private String host = "localhost";

  /**
   * RabbitMQ port. Ignored if an address is set. Default to 5672, or 5671 if SSL is
   * enabled.
   */
  private int port = 5672;

  /**
   * Login user to authenticate to the broker.
   */
  private String username = "guest";

  /**
   * Login to authenticate against the broker.
   */
  private String password = "guest";

  /**
   * Virtual host to use when connecting to the broker.
   */
  private String virtualHost = "/";
}
