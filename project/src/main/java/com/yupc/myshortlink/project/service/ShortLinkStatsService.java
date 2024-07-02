package com.yupc.myshortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yupc.myshortlink.project.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import com.yupc.myshortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.yupc.myshortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.yupc.myshortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.yupc.myshortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.yupc.myshortlink.project.dto.resp.ShortLinkStatsRespDTO;

public interface ShortLinkStatsService {
    /**
     * 获取单个短链接监控数据
     *
     * @param requestParam 获取短链接监控数据入参
     * @return 短链接监控数据
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam);

    IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam);

    ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 访问分组短链接指定时间内访问记录监控数据
     *
     * @param requestParam 获取分组短链接监控访问记录数据入参
     * @return 分组访问记录监控数据
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam);
}
