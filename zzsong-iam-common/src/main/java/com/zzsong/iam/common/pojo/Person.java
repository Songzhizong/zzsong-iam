package com.zzsong.iam.common.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * 人员信息
 *
 * @author 宋志宗 on 2022/6/1
 */
@Getter
@Setter
public class Person {
  private long id;

  /** 姓名 */
  private String name;

  /** 手机号 */
  private String phone;
}
