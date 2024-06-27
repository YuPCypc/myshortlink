package com.yupc.myshortlink.project.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ShortLinkUpdateReqDTO {
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
     * 完整短链接
     */
    private String fullShortUrl;

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
