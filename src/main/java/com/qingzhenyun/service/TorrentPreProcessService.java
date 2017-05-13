package com.qingzhenyun.service;

import com.qingzhenyun.constans.MqConst;
import com.qingzhenyun.jooq.common.generated.Tables;
import com.qingzhenyun.jooq.common.generated.tables.records.PreParseTorrentRecord;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * HoHo
 * Created by guna on 2017/5/13.
 */
@Service
public class TorrentPreProcessService extends BaseDslService {
    //public
    public void onTorrentFileAdded(HashMap<String, String> map) {
        //Find if exists..
        //PreParseTorrentRecord rec = dslContext.fetchOne(Tables.PRE_PARSE_TORRENT, Tables.PRE_PARSE_TORRENT.HASH.eq(hash));
        rabbitMessagingTemplate.convertAndSend(MqConst.OFFLINE_EXCHANGE, MqConst.OFFLINE_ADD_TORRENT_TASK_KEY, map);
    }

    @Autowired
    public void setRabbitMessagingTemplate(RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    private RabbitMessagingTemplate rabbitMessagingTemplate;
}
