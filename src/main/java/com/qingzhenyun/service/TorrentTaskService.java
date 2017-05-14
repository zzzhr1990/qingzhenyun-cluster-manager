package com.qingzhenyun.service;

import com.qingzhenyun.constans.TorrentConst;
import com.qingzhenyun.jooq.common.generated.Tables;
import com.qingzhenyun.jooq.common.generated.tables.records.WorkingTaskRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * JS
 * Created by guna on 2017/5/15.
 */
@Service
@Slf4j
public class TorrentTaskService extends BaseDslService {
    public void addNewTask(String infoHash, String sid, Integer percent, String taskName
            , Integer type, Integer remoteType, String bucket, String key, String url) {
        //Find if need new task
        long t = System.currentTimeMillis();
        WorkingTaskRecord workingTaskRecord = dslContext.fetchOne(Tables.WORKING_TASK, Tables.WORKING_TASK.INFO_HASH.eq(infoHash));
        if (workingTaskRecord != null) {
            workingTaskRecord.setSid(sid);
            log.info("Need to check working task");
            workingTaskRecord.setRefreshTime(t);
            workingTaskRecord.setPercent(percent);
            return;
        }
        workingTaskRecord = dslContext.newRecord(Tables.WORKING_TASK);
        workingTaskRecord.setInfoHash(infoHash);
        workingTaskRecord.setStatus(TorrentConst.STATUS_START_DOWNLOAD);
        workingTaskRecord.setAddTime(t);
        workingTaskRecord.setRefreshTime(t);
        workingTaskRecord.setPercent(percent);
        workingTaskRecord.setTaskName(taskName);
        workingTaskRecord.setType(type);
        if (url != null) {
            workingTaskRecord.setUrl(url);
        }
        workingTaskRecord.setRemoteType(1);
        if (bucket != null) {
            workingTaskRecord.setRemoveBucket(bucket);
        }
        if (key != null) {
            workingTaskRecord.setRemoveKey(key);
        }
        workingTaskRecord.store();
    }

    public void parseAndRefreshFiles() {

    }
}
