package im.eg.srb.rabbit.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
    @Bean
    public MessageConverter messageConverter() {
        // json 字符串转换器
        return new Jackson2JsonMessageConverter();
    }
}
