package com.qingzhenyun.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingzhenyun.constans.MqConst;
import com.qingzhenyun.service.TorrentPreProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

/**
 * AddLi
 * Created by guna on 2017/5/13.
 */
@Service
@Slf4j
public class OfflineTaskListener {
    @RabbitListener(bindings = @QueueBinding(value = @Queue,
            key = MqConst.OFFLINE_ADD_ROUTING_KEY,
            exchange = @Exchange(value = MqConst.OFFLINE_EXCHANGE, type = "direct", durable = "true", autoDelete = "false")))
    public void onOfflineTaskAdded(HashMap<String, String> info) {
        torrentPreProcessService.onTorrentFileAdded(info);
        log.info("Recv {}", toJsonString(info));
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue,
            key = MqConst.OFFLINE_TORRENT_PRE_PARSED_KEY,
            exchange = @Exchange(value = MqConst.OFFLINE_EXCHANGE, type = "direct", durable = "true", autoDelete = "false")))
    public void onOfflinePreParsed(JsonNode jsonNode) {
        boolean success = jsonNode.get("success").asBoolean();
        if (success) {
            String text = jsonNode.get("data").asText();
            try {
                JsonNode node = objectMapper.readTree(text);
                log.info(toJsonString(node));
            } catch (IOException e) {
                log.error("ex {}", e);
            }
        }
        //log.info("PreProcess {}", toJsonString(jsonNode));
    }

    @Autowired
    public void setTorrentPreProcessService(TorrentPreProcessService torrentPreProcessService) {
        this.torrentPreProcessService = torrentPreProcessService;
    }

    private TorrentPreProcessService torrentPreProcessService;
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
