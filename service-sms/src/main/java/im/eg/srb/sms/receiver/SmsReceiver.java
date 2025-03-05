package im.eg.srb.sms.receiver;

import im.eg.srb.base.dto.SmsDTO;
import im.eg.srb.rabbit.constant.MQConstant;
import im.eg.srb.sms.service.SmsService;
import im.eg.srb.sms.util.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SmsReceiver {
    @Resource
    private SmsService smsService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConstant.QUEUE_SMS_ITEM, durable = "true"), // 队列
            exchange = @Exchange(value = MQConstant.EXCHANGE_TOPIC_SMS), // 交换机
            key = {MQConstant.ROUTING_SMS_ITEM}// 路由键
    ))
    public void send(SmsDTO smsDTO) {
        log.info("消费消息：发送短信 {}", smsDTO);
        Map<String, Object> params = new HashMap<>();
        params.put("code", "0000");
        smsService.send(smsDTO.getMobile(), SmsProperties.TEMPLATE_CODE, params);
    }
}
