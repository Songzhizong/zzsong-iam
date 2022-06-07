package com.zzsong.iam.infrastructure.event;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationRunner;

/**
 * @author 宋志宗 on 2022/4/6
 */
public interface EventListener extends DisposableBean, ApplicationRunner {

}
