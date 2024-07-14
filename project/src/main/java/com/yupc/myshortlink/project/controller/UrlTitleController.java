package com.yupc.myshortlink.project.controller;

import com.yupc.myshortlink.project.common.convention.result.Result;
import com.yupc.myshortlink.project.common.convention.result.Results;
import com.yupc.myshortlink.project.service.UrlTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * URL标题控制层
 */
@RestController
@RequiredArgsConstructor
public class UrlTitleController {

    private final UrlTitleService urlTitleService;

    /**
     * 根据URL返回网站对应标题
     */
    @GetMapping("/api/short-link/v1/title")
    public Result<String> getTitleByUrl(@RequestParam("url") String url){
        return Results.success(urlTitleService.getTitleByUrl(url));
    }

}
