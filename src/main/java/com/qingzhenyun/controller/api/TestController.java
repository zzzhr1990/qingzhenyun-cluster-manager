package com.qingzhenyun.controller.api;

import com.qingzhenyun.exception.ApiException;
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

    @RequestMapping("/hoho")
    public HashMap<String, String> hoho() {

        throw new ApiException("Oh!");
        //return hash;
    }

    @RequestMapping("/oh")
    public ResponseEntity<String> oh() {
        return ResponseEntity.ok("OH");
    }
}
