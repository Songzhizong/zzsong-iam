package com.zzsong.iam.launcher;

import cn.idealframework.date.DateTimes;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 宋志宗 on 2021/7/4
 */
@Configuration
public class IdealBootWebMessageConverterAutoConfigure implements WebFluxConfigurer {

  @Override
  public void configureHttpMessageCodecs(@Nonnull ServerCodecConfigurer configurer) {
    SimpleModule JAVA_TIME_MODULE = new JavaTimeModule();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimes.YYYY_MM_DD_HH_MM_SS_SSS);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DateTimes.YYYY_MM_DD);
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(DateTimes.HH_MM_SS);
    JAVA_TIME_MODULE
      .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter))
      .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter))
      .addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter))
      .addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter))
      .addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter))
      .addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));

    // Long转String传输
    SimpleModule longToStrongModule = new SimpleModule();
    longToStrongModule.addSerializer(Long.class, ToStringSerializer.instance);
    longToStrongModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

    ObjectMapper objectMapper = new ObjectMapper()
      // 对于空的对象转json的时候不抛出错误
      .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
      // 禁用遇到未知属性抛出异常
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      // 序列化BigDecimal时不使用科学计数法输出
      .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
      .setDateFormat(new SimpleDateFormat(DateTimes.YYYY_MM_DD_HH_MM_SS_SSS))
      .registerModule(JAVA_TIME_MODULE)
      .registerModule(longToStrongModule)
      .findAndRegisterModules();
    // 序列化是忽略null值
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
    configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
  }
}
