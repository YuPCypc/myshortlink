package com.yupc.myshortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupc.myshortlink.admin.dao.entity.UserDO;
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

}
