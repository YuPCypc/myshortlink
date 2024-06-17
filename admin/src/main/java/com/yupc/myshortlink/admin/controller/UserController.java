package com.yupc.myshortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.yupc.myshortlink.admin.common.convention.result.Result;
import com.yupc.myshortlink.admin.common.convention.result.Results;
import com.yupc.myshortlink.admin.dto.resp.ActualUserRespDTO;
import com.yupc.myshortlink.admin.dto.resp.UserRespDTO;
import com.yupc.myshortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
/**
 * 用户管理控制层
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    /**
     * 根据用户名查找用户信息
     */
    @GetMapping("/api/myshortlink/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        UserRespDTO result = userService.getUserByUsername(username);
        return Results.success(result);
    }
    /**
     * 根据用户名查找用户无脱敏信息
     */
    @GetMapping("/api/myshortlink/v1/actual/user/{username}")
    public Result<ActualUserRespDTO> getActualUserByUsername(@PathVariable("username") String username) {
        ActualUserRespDTO result = BeanUtil.toBean(userService.getUserByUsername(username),ActualUserRespDTO.class);
        return Results.success(result);
    }
}
