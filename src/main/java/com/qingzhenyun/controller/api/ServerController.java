package com.qingzhenyun.controller.api;

import com.qingzhenyun.entity.ServerInfo;
import com.qingzhenyun.exception.ApiException;
import com.qingzhenyun.jooq.common.generated.tables.pojos.OnlineServer;
import com.qingzhenyun.service.ServerService;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Server
 * Created by guna on 2017/5/10.
 */
@RequestMapping("/api/server")
@RestController
public class ServerController {
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public OnlineServer register(@RequestBody ServerInfo serverInfo, HttpServletRequest req) {
        serverInfo.setIp(req.getRemoteAddr());
        return serverService.register(serverInfo);
    }

    @Autowired
    public void setServerService(ServerService serverService) {
        this.serverService = serverService;
    }

    private ServerService serverService;
}
