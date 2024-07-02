package com.yupc.myshortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yupc.myshortlink.admin.common.convention.result.Result;
import com.yupc.myshortlink.admin.remote.dto.ShortLinkRemoteService;
import com.yupc.myshortlink.admin.remote.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import com.yupc.myshortlink.admin.remote.dto.req.ShortLinkGroupStatsReqDTO;
import com.yupc.myshortlink.admin.remote.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.yupc.myshortlink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import com.yupc.myshortlink.admin.remote.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.yupc.myshortlink.admin.remote.dto.resp.ShortLinkStatsRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接监控控制层
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkStatsController {

    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService(){};


    /**
     * 访问单个短链接指定时间内监控数据
     */
    @GetMapping("/api/myshortlink/admin/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return shortLinkRemoteService.oneShortLinkStats(requestParam);
    }


    /**
     * 访问单个短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/myshortlink/admin/v1/stats/access-record")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        return shortLinkRemoteService.shortLinkStatsAccessRecord(requestParam);
    }

    /**
     * 访问分组短链接指定时间内监控数据
     */
    @GetMapping("/api/myshortlink/admin/v1/stats/group")
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam) {
        return shortLinkRemoteService.groupShortLinkStats(requestParam);
    }

    /**
     * 访问分组短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/myshortlink/v1/stats/access-record/group")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam) {
        return shortLinkRemoteService.groupShortLinkStatsAccessRecord(requestParam);
    }
}