package com.qingzhenyun.controller.api;

import com.qingzhenyun.exception.ApiException;
import com.qingzhenyun.service.TorrentPreProcessService;
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
    public boolean tryj() {

        torrentPreProcessService.getPreProcessExists("FqqOJo5Y9Aub6ZJGU022KZlzBRSl");
        //return hash;
        return true;
    }

    @RequestMapping("/try2")
    public boolean tryp(String hash) {

        torrentPreProcessService.getPreProcessExists(hash);
        //return hash;
        return true;
    }

    @RequestMapping("/oh")
    public ResponseEntity<String> oh() {
        return ResponseEntity.ok("OH");
    }

    @Autowired
    TorrentPreProcessService torrentPreProcessService;
}
