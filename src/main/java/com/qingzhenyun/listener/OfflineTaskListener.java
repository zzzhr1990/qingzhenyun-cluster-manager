package com.qingzhenyun.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingzhenyun.constans.MqConst;
import com.qingzhenyun.constans.TorrentConst;
import com.qingzhenyun.service.TorrentPreProcessService;
import com.qingzhenyun.service.TorrentTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue,
            key = MqConst.OFFLINE_TORRENT_PRE_PARSED_KEY,
            exchange = @Exchange(value = MqConst.OFFLINE_EXCHANGE, type = "direct", durable = "true", autoDelete = "false")))
    public void onOfflinePreParsed(JsonNode jsonNode) throws JsonProcessingException {
        try {
            boolean success = jsonNode.get("success").asBoolean();
            String urlHash = jsonNode.get("hash").asText();
            JsonNode info = jsonNode.get("info");
            String type = info.get("type").asText();
            String bucket = type.equals("torrent") ? info.get("bucket").asText() : null;
            String key = type.equals("torrent") ? info.get("key").asText() : null;
            String url = info.get("url").asText();
            String sid = info.get("sid").asText();
            Integer ct = 0;
            if (type.equals("torrent")) {
                ct = 1;
            }

            if (success) {
                JsonNode data = jsonNode.get("data");
                String infoHash = data.get("hash").asText();
                //Integer userId = data.get("userId").asInt();
                boolean c = torrentPreProcessService.createPreProcess(urlHash, infoHash, bucket, key, url, TorrentConst.PRE_PROCESS_SUCCESS);
                //TaskId is infohash
                if (!c) {
                    log.info("{} infoHash {} already exists", urlHash, infoHash);
                    //return;
                }
                String taskName = data.get("name").asText();
                Double percent = data.get("progress").asDouble();
                torrentTaskService.addNewTask(infoHash, sid, (int) (Math.floor(percent)), taskName, 0, ct, bucket, key, url);
                //Loop to add files..
                //TODO: check dump..
                //Check and update
                torrentTaskService.parseAndRefreshFiles(infoHash, sid, 0, sid, 0, data.get("files"), data.get("file_priorities"), data.get("file_progress"));

            } else {
                torrentPreProcessService.onTorrentPreProcessFailed(urlHash, jsonNode.get("status").asInt());
            }
        } catch (Exception e) {
            log.error("Exec listener ex", e);
        }
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

    @Autowired
    public void setTaskService(TorrentTaskService torrentTaskService) {
        this.torrentTaskService = torrentTaskService;
    }

    private TorrentTaskService torrentTaskService;
    //Queue(),key = MqConst.OFFLINE_ADD_ROUTING_KEY, exchange = Exchange(MqConst.OFFLINE_EXCHANGE,type = "direct",durable = "true",autoDelete = "false")
}
