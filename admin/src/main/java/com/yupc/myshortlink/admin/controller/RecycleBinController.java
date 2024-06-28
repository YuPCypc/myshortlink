package com.yupc.myshortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yupc.myshortlink.admin.common.convention.result.Result;
import com.yupc.myshortlink.admin.common.convention.result.Results;
import com.yupc.myshortlink.admin.dto.req.RecycleBinSaveReqDTO;
import com.yupc.myshortlink.admin.remote.dto.ShortLinkRemoteService;
import com.yupc.myshortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.yupc.myshortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecycleBinController {

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
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam){
        return shortLinkRemoteService.RecycleBinpageShortLink(requestParam);
    }
}
