package com.qingzhenyun.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingzhenyun.constans.MqConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AddLi
 * Created by guna on 2017/5/13.
 */
@Service
@Slf4j
public class OfflineTaskAddListener {
    @RabbitListener(bindings = @QueueBinding(value = @Queue,
            key = MqConst.OFFLINE_ADD_ROUTING_KEY,
            exchange = @Exchange(value = MqConst.OFFLINE_EXCHANGE, type = "direct", durable = "true", autoDelete = "false")))
    public void onOfflineTaskAdded(JsonNode jsonNode) {
        log.info("Recv {}", toJsonString(jsonNode));
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJsonString(Object str) {
        try {
            return objectMapper.writeValueAsString(str);
        } catch (JsonProcessingException e) {
            log.error("An unexpected error occurred.", e);
        }
        return "";
    }

    private ObjectMapper objectMapper;
    //Queue(),key = MqConst.OFFLINE_ADD_ROUTING_KEY, exchange = Exchange(MqConst.OFFLINE_EXCHANGE,type = "direct",durable = "true",autoDelete = "false")
}
