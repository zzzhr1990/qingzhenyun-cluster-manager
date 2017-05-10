package com.qingzhenyun.service;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Ser
 * Created by guna on 2017/5/10.
 */
public abstract class BaseDslService {

    @Autowired
    public void setDslContext(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public DSLContext dslContext;
}
