package com.zzsong.iam.server.domain.model.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author 宋志宗 on 2022/2/23
 */
@Getter
@Setter
public class User {

  private long id;

  private String name;

  private String account;

  private String email;

  private String phone;

  private boolean frozen;

  private LocalDateTime createdTime;

  private LocalDateTime updatedTime;
}
