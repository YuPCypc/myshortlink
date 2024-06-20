package com.yupc.myshortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.yupc.myshortlink.admin.common.convention.result.Result;
import com.yupc.myshortlink.admin.common.convention.result.Results;
import com.yupc.myshortlink.admin.dto.req.UserLoginReqDTO;
import com.yupc.myshortlink.admin.dto.req.UserRegisterReqDTO;
import com.yupc.myshortlink.admin.dto.req.UserUpdateReqDTO;
import com.yupc.myshortlink.admin.dto.resp.ActualUserRespDTO;
import com.yupc.myshortlink.admin.dto.resp.UserLoginRespDTO;
import com.yupc.myshortlink.admin.dto.resp.UserRespDTO;
import com.yupc.myshortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/api/myshortlink/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        UserRespDTO result = userService.getUserByUsername(username);
        return Results.success(result);
    }

    /**
     * 根据用户名查找用户无脱敏信息
     */
    @GetMapping("/api/myshortlink/admin/v1/actual/user/{username}")
    public Result<ActualUserRespDTO> getActualUserByUsername(@PathVariable("username") String username) {
        ActualUserRespDTO result = BeanUtil.toBean(userService.getUserByUsername(username), ActualUserRespDTO.class);
        return Results.success(result);
    }

    /**
     * 根据用户名查看用户是否已存在
     */
    @GetMapping("/api/myshortlink/admin/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username) {
        return Results.success(userService.hasUsername(username));
    }

    /**
     * 注册用户
     *
     * @param requestParam 注册用户参数
     */
    @PostMapping("/api/myshortlink/admin/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    /**
     * 更新用户信息
     * @param requestParam
     * @return
     */
    @PutMapping("/api/myshortlink/admin/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDTO requestParam) {
        userService.update(requestParam);
        return Results.success();
    }

    /**
     * 用户登陆
     * @param requestParam
     * @return
     */
    @PostMapping("/api/myshortlink/admin/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        return Results.success(userService.login(requestParam));
    }

    /**
     * 检查用户是否登陆
     */
    @GetMapping("/api/myshortlink/admin/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("username") String username,@RequestParam("token") String token) {
        return Results.success(userService.checkLogin(username,token));
    }

    /**
     * 退出登陆
     */
    @DeleteMapping("/api/myshortlink/admin/v1/user/logout")
    public Result<Void> logout(@RequestParam("username") String username,@RequestParam("token") String token) {
        userService.logout(username,token);
        return Results.success();
    }
}
