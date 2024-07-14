package com.yupc.myshortlink.admin.controller;

import com.yupc.myshortlink.admin.common.convention.result.Result;
import com.yupc.myshortlink.admin.common.convention.result.Results;
import com.yupc.myshortlink.admin.dto.req.ShortLinkGroupDeleteReqDTO;
import com.yupc.myshortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import com.yupc.myshortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.yupc.myshortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.yupc.myshortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.yupc.myshortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 短链接分组控制层
 */
@RequiredArgsConstructor
@RestController
public class GroupController {
    private final GroupService groupService;

    /**
     * 新建短链接分组
     */
    @PostMapping("/api/short-link/admin/v1/group")
    public Result<Void> getUserByUsername(@RequestBody ShortLinkGroupSaveReqDTO requestParam) {
        groupService.saveGroup (requestParam.getName());
        return Results.success();
    }

    /**
     * 获取短链接分组列表
     */
    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> listGroup() {
        return Results.success(groupService.listGroup());
    }

    /**
     * 更新短链接分组
     */
    @PutMapping("/api/short-link/admin/v1/group")
    public Result<Void> updateGroup(@RequestBody ShortLinkGroupUpdateReqDTO requestParam) {
        groupService.updateGroup(requestParam);
        return Results.success();
    }

    /**
     * 删除短链接分组
     */
    @DeleteMapping("/api/short-link/admin/v1/group")
    public Result<Void> deleteGroup(@RequestBody ShortLinkGroupDeleteReqDTO requestParam) {
        groupService.deleteGroup(requestParam);
        return Results.success();
    }

    /**
     * 更改短链接分组排序
     */

    @PostMapping("/api/short-link/admin/v1/group/sort")
    public Result<Void> sortGroup(@RequestBody List<ShortLinkGroupSortReqDTO> requestParam){
        groupService.sortGroup(requestParam);
        return Results.success();
    }
}
