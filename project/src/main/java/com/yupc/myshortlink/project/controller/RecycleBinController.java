package com.yupc.myshortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yupc.myshortlink.project.common.convention.result.Result;
import com.yupc.myshortlink.project.common.convention.result.Results;
import com.yupc.myshortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.yupc.myshortlink.project.dto.req.ShortLinkPageReqDTO;
import com.yupc.myshortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.yupc.myshortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinService recycleBinService;
    /**
     * 回收站添加功能
     */
    @PostMapping("/api/myshortlink/v1/recycle-bin")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam){
        recycleBinService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 分页查询回收站功能
     */

    @GetMapping("/api/myshortlink/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam){
        return Results.success(recycleBinService.pageShortLink(requestParam));
    }
}
