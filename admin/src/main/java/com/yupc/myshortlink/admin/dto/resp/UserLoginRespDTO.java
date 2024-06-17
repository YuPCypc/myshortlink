package com.yupc.myshortlink.admin.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登陆接口
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRespDTO {
    /**
     * 用户token
     */
    private String token;
}
