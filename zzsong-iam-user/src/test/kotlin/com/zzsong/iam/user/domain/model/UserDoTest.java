package com.zzsong.iam.user.domain.model;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author 宋志宗 on 2022/6/7
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserDoTest {
  private static final String account = "zzsong";
  private static String encryptAccount;

  private static final String phone = "18888888888";
  private static String encryptPhone;

  private static final String email = "18888888888@163.com";
  private static String encryptEmail;

  @Test
  public void t1EncryptAccount() {
    encryptAccount = UserDo.encryptAccount(account);
    System.out.println(encryptAccount);
  }

  @Test
  public void t2DecryptAccount() {
    String decryptAccount = UserDo.decryptAccount(encryptAccount);
    Assert.assertEquals(decryptAccount, account);
  }

  @Test
  public void t3EncryptPhone() {
    encryptPhone = UserDo.encryptPhone(phone);
    System.out.println(encryptPhone);
  }

  @Test
  public void t4DecryptPhone() {
    String decryptPhone = UserDo.decryptPhone(encryptPhone);
    Assert.assertEquals(decryptPhone, phone);
  }

  @Test
  public void t5EncryptEmail() {
    encryptEmail = UserDo.encryptEmail(email);
    System.out.println(encryptEmail);

  }

  @Test
  public void t6DecryptEmail() {
    String decryptEmail = UserDo.decryptEmail(encryptEmail);
    Assert.assertEquals(decryptEmail, email);

  }
}
