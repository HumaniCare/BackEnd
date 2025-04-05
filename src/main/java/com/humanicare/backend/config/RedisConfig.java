package com.humanicare.backend.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * RedisConfig
 *
 * - JWT 전용 Redis (인증, 토큰 저장 등)
 * - FastAPI와 공유하는 Redis (데이터 연동)
 * 두 개의 Redis 인스턴스를 사용하는 설정 클래스입니다.
 */
@Configuration
public class RedisConfig {

    // -------------------------------
    // ✅ JWT 전용 Redis 설정
    // -------------------------------

    /**
     * JWT Redis용 연결 팩토리 (spring.data.redis.jwt.* 값을 기반으로 생성됨)
     */
    @Bean(name = "jwtRedisConnectionFactory")
    @ConfigurationProperties(prefix = "spring.data.redis.jwt")
    public LettuceConnectionFactory jwtRedisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    /**
     * JWT RedisTemplate - 토큰 저장에 사용됨
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> jwtRedisTemplate(
            @Qualifier("jwtRedisConnectionFactory") RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        configureSerializers(template); // 직렬화 설정 공통 메서드
        return template;
    }

    // -------------------------------
    // ✅ FastAPI 연동용 Redis 설정
    // -------------------------------

    /**
     * FastAPI 공유용 Redis 연결 팩토리 (spring.data.redis.schedule.* 값을 기반으로 생성됨)
     */
    @Bean(name = "scheduleRedisConnectionFactory")
    @ConfigurationProperties(prefix = "spring.data.redis.schedule")
    public LettuceConnectionFactory scheduleRedisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    /**
     * FastAPI와 공유할 RedisTemplate - 데이터 연동 및 메시지 전달에 사용됨
     */
    @Bean(name = "scheduleRedisTemplate")
    public RedisTemplate<String, Object> scheduleRedisTemplate(
            @Qualifier("scheduleRedisConnectionFactory") RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        configureSerializers(template); // 직렬화 설정 공통 메서드
        return template;
    }

    // -------------------------------
    // 🔄 공통 직렬화 설정 메서드
    // -------------------------------

    /**
     * RedisTemplate 직렬화 공통 설정
     * - Key: 문자열
     * - Value: JSON 직렬화 (객체 저장 시 사용)
     */
    private void configureSerializers(RedisTemplate<String, Object> template) {
        template.setKeySerializer(new StringRedisSerializer()); // 키는 항상 문자열
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // 객체 저장 시 JSON 변환
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.setDefaultSerializer(new StringRedisSerializer()); // 모든 기본값은 문자열
    }
}
