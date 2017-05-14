package com.qingzhenyun.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.qingzhenyun.constans.MqConst;
import com.qingzhenyun.constans.TorrentConst;
import com.qingzhenyun.jooq.common.generated.Tables;
import com.qingzhenyun.jooq.common.generated.tables.pojos.PreParseTorrent;
import com.qingzhenyun.jooq.common.generated.tables.records.PreParseTorrentRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * HoHo
 * Created by guna on 2017/5/13.
 */
@Slf4j
@Service
public class TorrentPreProcessService extends BaseDslService {
    //public
    public void onTorrentFileAdded(HashMap<String, String> map) {
        //Find if exists..
        //PreParseTorrentRecord rec = dslContext.fetchOne(Tables.PRE_PARSE_TORRENT, Tables.PRE_PARSE_TORRENT.HASH.eq(hash));
        rabbitMessagingTemplate.convertAndSend(MqConst.OFFLINE_EXCHANGE, MqConst.OFFLINE_ADD_TORRENT_TASK_KEY, map);
    }

    public boolean onTorrentPreProcessSuccess(String fileHash, String infoHash, String bucket, String key, String url) {
        //If not have, we should create new one
        boolean create = false;
        PreParseTorrentRecord fetch = dslContext.fetchOne(Tables.PRE_PARSE_TORRENT, Tables.PRE_PARSE_TORRENT.HASH.eq(fileHash));
        if (fetch == null) {
            fetch = dslContext.newRecord(Tables.PRE_PARSE_TORRENT);
            fetch.setHash(fileHash);
            fetch.setStoreType(0);
            fetch.setInfohash(infoHash);
            fetch.setStoreBucket(bucket);
            fetch.setStoreKey(key);
            fetch.setUrl(url);
            create = true;
        }
        fetch.setStatus(TorrentConst.PRE_PROCESS_SUCCESS);
        fetch.setTryTime(System.currentTimeMillis());
        fetch.store();
        return create;
    }

    public void onTorrentPreProcessFailed(String fileHash, Integer status) {
        //If not have, we should create new one
        PreParseTorrentRecord fetch = dslContext.fetchOne(Tables.PRE_PARSE_TORRENT, Tables.PRE_PARSE_TORRENT.HASH.eq(fileHash));
        if (fetch == null) {
            log.warn("{} task not found", fileHash);
            return;
        }
        fetch.setStatus(status);
        fetch.setTryTime(System.currentTimeMillis());
        fetch.store();
    }

    @Autowired
    public void setRabbitMessagingTemplate(RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    private RabbitMessagingTemplate rabbitMessagingTemplate;
}
