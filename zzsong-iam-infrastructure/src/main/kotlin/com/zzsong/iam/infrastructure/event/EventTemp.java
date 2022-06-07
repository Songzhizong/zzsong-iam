package com.zzsong.iam.infrastructure.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/4/1
 */
@Getter
@Setter
@Document("ideal_event_publish_temp")
public class EventTemp {
  @Id
  private long id;
  @Nonnull
  private String eventInfo;
  private long timestamp;
}
