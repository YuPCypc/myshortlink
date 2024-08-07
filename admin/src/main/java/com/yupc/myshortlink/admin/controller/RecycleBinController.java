package com.yupc.myshortlink.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupc.myshortlink.admin.common.convention.result.Result;
import com.yupc.myshortlink.admin.common.convention.result.Results;
import com.yupc.myshortlink.admin.dto.req.RecycleBinRecoverReqDTO;
import com.yupc.myshortlink.admin.dto.req.RecycleBinRemoveReqDTO;
import com.yupc.myshortlink.admin.dto.req.RecycleBinSaveReqDTO;
import com.yupc.myshortlink.admin.remote.dto.ShortLinkActualRemoteService;
import com.yupc.myshortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.yupc.myshortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.yupc.myshortlink.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecycleBinController {


    private final RecycleBinService recycleBinService;

    private final ShortLinkActualRemoteService shortLinkActualRemoteService;

    /**
     * 回收站添加功能
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam){
        shortLinkActualRemoteService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 回收站分页返回
     */
    @GetMapping("/api/short-link/admin/v1/recycle-bin/page")
    public Result<Page<ShortLinkPageRespDTO>> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        return recycleBinService.pageRecycleBinShortLink(requestParam);
    }

    /**
     * 回收站恢复功能
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam){
        shortLinkActualRemoteService.recoverRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 回收站删除
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/remove")
    public Result<Void> removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO requestParam){
        shortLinkActualRemoteService.removeRecycleBin(requestParam);
        return Results.success();
    }
}
