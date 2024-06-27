package com.yupc.myshortlink.admin.remote.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ShortLinkCreateReqDTO {


    /**
     * 域名
     */
    private String domain;

    /**
     * gid
     */
    private String gid;


    /**
     * 原始链接
     */
    private String originUrl;


    /**
     * 创建类型：0:接口创建n1:控制台创建
     */
    private int createType;

    /**
     * 有效期类型：0:永久有效n1:自定义有效
     */
    private int validDateType;

    /**
     * 有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date validTime;

    /**
     * 描述
     */
    private String describe;

}
