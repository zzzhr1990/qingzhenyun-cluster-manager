package com.qingzhenyun.controller.api;

import com.qingzhenyun.exception.ApiException;
import com.qingzhenyun.jooq.common.generated.Tables;
import com.qingzhenyun.service.TorrentPreProcessService;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * Controller for test
 * Created by guna on 2017/5/9.
 */
@RequestMapping("/api/test")
@RestController
public class TestController {
    @RequestMapping("/haha")
    public HashMap<String, String> haha() {
        HashMap<String, String> hash = new HashMap<>();
        hash.put("A", "b");
        return hash;
    }

    @RequestMapping("/try")
    public boolean tryj(String hash) {

        torrentPreProcessService.getPreProcessExists(hash);
        //return hash;
        return torrentPreProcessService.getPreProcessExists(hash) == null;
    }

    @RequestMapping("/exists")
    public boolean exists(Integer id) {
        return dslContext.fetchOne(Tables.QZY_TEST_CASE, Tables.QZY_TEST_CASE.ID.eq(id)) == null;
    }

    @RequestMapping("/oh")
    public ResponseEntity<String> oh() {
        return ResponseEntity.ok("OH");
    }

    @Autowired
    TorrentPreProcessService torrentPreProcessService;

    @Autowired
    public void setDslContext(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    protected DSLContext dslContext;
}
