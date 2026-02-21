package com.api.blog.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.URI;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.url}")
    String redisUrl;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {

        URI uri = URI.create(redisUrl);

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();

        config.setHostName(uri.getHost());
        config.setPort(uri.getPort());

        // userInfo = "default:password"
        String[] userInfo = uri.getUserInfo().split(":");
        config.setUsername(userInfo[0]);
        config.setPassword(userInfo[1]);

        JedisClientConfiguration clientConfig = JedisClientConfiguration.builder()
                        .useSsl()
                        .build();

        return new JedisConnectionFactory(config, clientConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
