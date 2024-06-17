package com.yupc.myshortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupc.myshortlink.admin.dao.entity.UserDO;
import com.yupc.myshortlink.admin.dto.req.UserRegisterReqDTO;
import com.yupc.myshortlink.admin.dto.req.UserUpdateReqDTO;
import com.yupc.myshortlink.admin.dto.resp.UserRespDTO;

/**
 * 用户接口层
 */
public interface UserService extends IService<UserDO> {
    /**
     * 根据用户名查找用户信息
     * @param username
     * @return 用户返回实体
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 查询用户名是否已存在
     * @param username
     * @return 用户名不存在返回 True，存在返回 False
     */
    Boolean hasUsername(String username);

    /**
     * 注册用户
     * @param requestParam 注册用户参数
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 修改用户
     * @param requestParam 修改用户参数
     */
    void update(UserUpdateReqDTO requestParam);


}
