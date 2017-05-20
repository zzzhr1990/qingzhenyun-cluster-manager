package com.qingzhenyun.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.qingzhenyun.constans.TorrentConst;
import com.qingzhenyun.entity.DownloadingFile;
import com.qingzhenyun.jooq.common.generated.Tables;
import com.qingzhenyun.jooq.common.generated.tables.records.TorrentFileInfoRecord;
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
            String lastSid = workingTaskRecord.getSid();
            if (!lastSid.equals(sid)) {
                log.info("Need to check working task {} and {} on [{}]", lastSid, sid, infoHash);
            }
            workingTaskRecord.setSid(sid);
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
        workingTaskRecord.setFinished(0);
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

    public void parseAndRefreshFiles(String infoHash, String sid, Integer finished,
                                     String serverId, Integer status, JsonNode files, JsonNode downloadProps, JsonNode progressProps) {
        files.forEach((fileInfo) -> {
            DownloadingFile file = new DownloadingFile();
            int index = fileInfo.get("index").asInt();
            file.setIndex(index);
            file.setSize(fileInfo.get("size").asLong());
            file.setPath(fileInfo.get("path").asText());
            file.setNeedDownload(downloadProps.get(index).asInt());
            file.setProgress((int) progressProps.get(index).asDouble() * 100);
            refreshFile(infoHash, sid, file.getPath(), file.getSize(), index, file.getNeedDownload(), finished, serverId, file.getProgress(), status);
        });
    }

    public void refreshFile(String infoHash, String sid,
                            String filePath, Long fileSize,
                            Integer fileIndex, Integer needDownload, Integer finished,
                            String serverId, Integer progress, Integer status) {
        //First find available
        long current = System.currentTimeMillis();
        TorrentFileInfoRecord rec = dslContext.fetchOne(Tables.TORRENT_FILE_INFO, Tables.TORRENT_FILE_INFO.INFO_HASH.eq(infoHash).and(Tables.TORRENT_FILE_INFO.FILE_INDEX.eq(fileIndex)));
        if (rec == null) {
            rec = dslContext.newRecord(Tables.TORRENT_FILE_INFO);
            rec.setAddTime(current);
            rec.setInfoHash(infoHash);
            rec.setFilePath(filePath);
            rec.setFileSize(fileSize);
            rec.setFileIndex(fileIndex);
            rec.setNeedDownload(needDownload);
        }
        rec.setFinished(checkValue(rec.getFinished(), finished));
        rec.setRefreshTime(current);
        rec.setServerId(serverId);
        rec.setProgress(checkValue(rec.getProgress(), progress));
        rec.setStatus(checkValue(rec.getStatus(), status));
        rec.store();
    }

    private Integer checkValue(Integer oldVal, Integer newVal) {
        if (oldVal == null) {
            return newVal;
        }
        if (oldVal > newVal) {
            log.warn("CHECK!!VAL_BACK!!!");
            return oldVal;
        }
        return newVal;
    }
}
