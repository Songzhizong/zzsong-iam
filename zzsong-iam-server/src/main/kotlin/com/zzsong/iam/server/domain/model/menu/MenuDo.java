package com.zzsong.iam.server.domain.model.menu;

import com.zzsong.iam.common.constants.MenuType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

/**
 * @author 宋志宗 on 2022/2/22
 */
@Getter
@Setter
@Table("iam_menu")
public class MenuDo {

  /** 主键 */
  @Id
  private long id = -1;

  /** 父菜单id */
  private long pid;

  /** 归属的终端id */
  private long terminalId;

  /** 数路由 */
  @Nonnull
  private String router = "";

  /** 菜单名称 */
  @Nonnull
  private String name = "";

  /** 菜单类型 */
  @Nonnull
  private MenuType type;

  /** 访问的接口地址列表 */
  @Nonnull
  private String apis;

  /** 图标 */
  @Nonnull
  private String icon;

  @Nonnull
  private String path;

  @Nonnull
  private String url;

  /** 乐观锁版本 */
  @Version
  private long version = 0;

  /** 创建时间 */
  @CreatedDate
  private LocalDateTime createdTime;

  /** 更新时间 */
  @LastModifiedDate
  private LocalDateTime updatedTime;
}
