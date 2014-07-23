package com.clemble.casino.server.spring.payment;

import com.clemble.casino.server.repository.payment.JedisPlayerAccountTemplate;
import com.clemble.casino.server.spring.common.RedisSpringConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.clemble.casino.server.player.account.BasicServerPlayerAccountService;
import com.clemble.casino.server.player.account.ServerPlayerAccountService;
import com.clemble.casino.server.repository.payment.PlayerAccountTemplate;
import com.clemble.casino.server.spring.common.CommonSpringConfiguration;
import com.clemble.casino.server.spring.common.SpringConfiguration;
import redis.clients.jedis.JedisPool;

@Configuration
@Import({
    CommonSpringConfiguration.class,
    PaymentMongoSpringConfiguration.class,
    RedisSpringConfiguration.class})
public class PaymentManagementSpringConfiguration implements SpringConfiguration {

    @Bean
    public PlayerAccountTemplate playerAccountTemplate(JedisPool jedisPool) {
        return new JedisPlayerAccountTemplate(jedisPool);
    }

    @Bean
    public ServerPlayerAccountService realPlayerAccountService(PlayerAccountTemplate playerAccountRepository) {
        return new BasicServerPlayerAccountService(playerAccountRepository);
    }

}
