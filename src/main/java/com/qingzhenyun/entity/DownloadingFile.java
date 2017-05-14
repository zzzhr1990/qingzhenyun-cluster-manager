package com.qingzhenyun.entity;

import lombok.Data;

/**
 * Ho
 * Created by guna on 2017/5/15.
 */
@Data
public class DownloadingFile {
    private int index;
    private long size;
    private String path;
    private int needDownload = 1;
    private int progress = 0;
}
