package com.qingzhenyun.service;

import com.qingzhenyun.entity.ServerInfo;
import com.qingzhenyun.exception.ApiException;
import com.qingzhenyun.jooq.common.generated.tables.pojos.OnlineServer;
import com.qingzhenyun.jooq.common.generated.tables.records.OnlineServerRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static com.qingzhenyun.jooq.common.generated.Tables.ONLINE_SERVER;

/**
 * N
 * Created by guna on 2017/5/10.
 */
@Service
@Slf4j
public class ServerService extends BaseDslService {
    public OnlineServer register(ServerInfo serverInfo) {
        String sid = serverInfo.getSid();
        if (StringUtils.isEmpty(sid)) {
            throw new ApiException("SID_MUST_NOT_BE_NULL_OR_EMPTY");
        }
        OnlineServerRecord onlineServerRecord = dslContext.fetchOne(ONLINE_SERVER, ONLINE_SERVER.SID.eq(sid));
        if (onlineServerRecord == null) {
            onlineServerRecord = dslContext.newRecord(ONLINE_SERVER);
            onlineServerRecord.setSid(sid);
        }
        configServer(serverInfo, onlineServerRecord);
        onlineServerRecord.store();

        log.info("{} server registered.");
        /*
        Optional<OnlineServer> onlineServer = this.dslContext.selectOne()
                .from(ONLINE_SERVER).where(ONLINE_SERVER.SID.eq(sid))
                .fetchOptionalInto(OnlineServer.class);
        if(onlineServer.isPresent()){
            OnlineServer server = onlineServer.get();
            configServer(serverInfo,server);
            //new OnlineServerRecord().from(serverInfo);

            return dslContext.update(ONLINE_SERVER).set()
        }else{
            OnlineServer server = new OnlineServer();
            server.setSid(sid);
            configServer(serverInfo,server);
        }
        */
        return onlineServerRecord.into(OnlineServer.class);
    }

    private void configServer(ServerInfo serverInfo, OnlineServerRecord onlineServer) {
        String name = serverInfo.getName();
        long current = System.currentTimeMillis();
        String ip = serverInfo.getIp();
        onlineServer.setIp(ip);
        onlineServer.setName(name);
        onlineServer.setConnectTime(current);
        onlineServer.setLastHeartbeatTime(current);
        onlineServer.setIdleWorker(0);
        onlineServer.setWorkerCount(0);

    }
}
