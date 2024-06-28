package com.yupc.myshortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yupc.myshortlink.admin.common.convention.result.Result;
import com.yupc.myshortlink.admin.common.convention.result.Results;
import com.yupc.myshortlink.admin.dto.req.RecycleBinRecoverReqDTO;
import com.yupc.myshortlink.admin.dto.req.RecycleBinSaveReqDTO;
import com.yupc.myshortlink.admin.remote.dto.ShortLinkRemoteService;
import com.yupc.myshortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.yupc.myshortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.yupc.myshortlink.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RecycleBinController {


    private final RecycleBinService recycleBinService;
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService(){};

    /**
     * 回收站添加功能
     */
    @PostMapping("/api/myshortlink/admin/v1/recycle-bin")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam){
        shortLinkRemoteService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 回收站分页返回
     */
    @GetMapping("/api/myshortlink/admin/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam){
        return recycleBinService.pageShortLink(requestParam);
    }

    /**
     * 回收站恢复功能
     */
    @PostMapping("/api/myshortlink/admin/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam){
        shortLinkRemoteService.recoverRecycleBin(requestParam);
        return Results.success();
    }
}
