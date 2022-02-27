package com.zzsong.iam.server.domain.model.tenant;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author 宋志宗 on 2022/2/26
 */
@Getter
@Setter
public class Tenant {
  private long id;

  private long pid;

  private String name;

  private long ownerUserId;

  private boolean frozen;

  private LocalDateTime createdTime;

  private LocalDateTime updatedTime;
}
