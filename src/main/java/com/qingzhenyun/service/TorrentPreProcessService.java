package com.qingzhenyun.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.qingzhenyun.constans.MqConst;
import com.qingzhenyun.constans.TorrentConst;
import com.qingzhenyun.jooq.common.generated.Tables;
import com.qingzhenyun.jooq.common.generated.tables.pojos.PreParseTorrent;
import com.qingzhenyun.jooq.common.generated.tables.records.PreParseTorrentRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Result;
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
        String fileHash = map.get("hash");
        //Find if exists..
        PreParseTorrentRecord rec = getPreProcessExists(fileHash);
        if (rec != null) {
            return;
        }
        String type = map.get("type");
        // find if there is torrent already have info.

        String bucket = map.get("bucket");
        String infoHash = map.get("infoHash");
        if (type.equals("magnet")) {
            Result<PreParseTorrentRecord> fetch = dslContext.fetch(Tables.PRE_PARSE_TORRENT, Tables.PRE_PARSE_TORRENT.INFOHASH.eq(infoHash));
            for (PreParseTorrentRecord torrentInfo : fetch) {
                if (torrentInfo.getStoreKey() != null) {
                    createPreProcess(fileHash, infoHash, torrentInfo.getStoreBucket(),
                            torrentInfo.getStoreKey(), torrentInfo.getUrl(), torrentInfo.getStatus());
                    return;
                }
            }
        }
        String key = map.get("key");
        String url = map.get("url");
        //Create F
        createPreProcess(fileHash, infoHash, bucket, key, url, TorrentConst.PRETENDING_PROCESS_SUCCESS);
        rabbitMessagingTemplate.convertAndSend(MqConst.OFFLINE_EXCHANGE, MqConst.OFFLINE_ADD_TORRENT_TASK_KEY, map);
    }

    public void onMagnetUrlAdded(HashMap<String, String> map) {
        String fileHash = map.get("hash");
        //Find if exists..
        PreParseTorrentRecord rec = dslContext.fetchOne(Tables.PRE_PARSE_TORRENT, Tables.PRE_PARSE_TORRENT.HASH.eq(fileHash));
        if (rec != null) {
            return;
        }
        rabbitMessagingTemplate.convertAndSend(MqConst.OFFLINE_EXCHANGE, MqConst.OFFLINE_ADD_TORRENT_TASK_KEY, map);
    }

    public PreParseTorrentRecord getPreProcessExists(String fileHash) {
        return dslContext.fetchOne(Tables.PRE_PARSE_TORRENT, Tables.PRE_PARSE_TORRENT.HASH.eq(fileHash));
    }

    public boolean createPreProcess(String fileHash, String infoHash, String bucket, String key, String url, Integer status) {
        //If not have, we should create new one
        boolean create = false;
        PreParseTorrentRecord fetch = getPreProcessExists(fileHash);
        if (fetch == null) {
            fetch = dslContext.newRecord(Tables.PRE_PARSE_TORRENT);
            fetch.setHash(fileHash);
            fetch.setStoreType(0);
            create = true;
        }
        if (infoHash != null) {
            fetch.setInfohash(infoHash);
        }
        if (bucket != null) {
            fetch.setStoreBucket(bucket);
        }
        if (key != null) {
            fetch.setStoreKey(key);
        }
        if (url != null) {
            fetch.setUrl(url);
        }
        Integer st = fetch.getStatus();
        if (st < status) {
            fetch.setStatus(status);
        }
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
