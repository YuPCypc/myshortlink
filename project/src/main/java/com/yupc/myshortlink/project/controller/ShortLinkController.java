package com.yupc.myshortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yupc.myshortlink.project.common.convention.result.Result;
import com.yupc.myshortlink.project.common.convention.result.Results;
import com.yupc.myshortlink.project.dto.req.ShortLinkBatchCreateReqDTO;
import com.yupc.myshortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.yupc.myshortlink.project.dto.req.ShortLinkPageReqDTO;
import com.yupc.myshortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.yupc.myshortlink.project.dto.resp.ShortLinkBatchCreateRespDTO;
import com.yupc.myshortlink.project.dto.resp.ShortLinkCountQueryRespDTO;
import com.yupc.myshortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.yupc.myshortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.yupc.myshortlink.project.service.ShortLinkService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShortLinkController {
    private final ShortLinkService shortLinkService;

    /**
     * 新建短链接
     */
    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam){
        return Results.success(shortLinkService.createShortLink(requestParam));
    }

    /**
     * 更新短链接
     */
    @PostMapping("/api/short-link/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam){
        shortLinkService.updateShortLink(requestParam);
        return Results.success();
    }

    /**
     * 短链接分页返回
     */
    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam){
        return Results.success(shortLinkService.pageShortLink(requestParam));
    }

    /**
     * 查询短链接分组内数量
     */
    @GetMapping("/api/short-link/v1/count")
    public Result<List<ShortLinkCountQueryRespDTO>> listGroupShortLinkCount(@RequestParam("requestParam") List<String> requestParam){
        return Results.success(shortLinkService.listGroupShortLinkCount(requestParam));
    }

    /**
     * 存储短链接跳转信息
     */
    @GetMapping("/{short-url}")
    public void restoreUrl(@PathVariable("short-url")String shortUrl, ServletRequest serverHttpRequest, ServletResponse serverHttpResponse){
        shortLinkService.restoreUrl(shortUrl,serverHttpRequest,serverHttpResponse);
    }

    /**
     * 批量创建短链接
     */
    @PostMapping("/api/short-link/v1/create/batch")
    public Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam) {
        return Results.success(shortLinkService.batchCreateShortLink(requestParam));
    }


}
