package com.yupc.myshortlink.project.dto.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 短链接分页返回参数
 */
@Data
public class ShortLinkPageRespDTO {
    /**
     * id
     */
    private Long id;

    /**
     * 域名
     */
    private String domain;

    /**
     * 短链接
     */
    private String shortUrl;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 点击量
     */
    private Integer clickNum;


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
    @TableField("`describe`")
    private String describe;

    /**
     * 分组id
     */
    private String gid;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * 网站图标
     */
    private String favicon;

    /**
     * 历史PV
     */
    private Integer totalPv;

    /**
     * 今日PV
     */
    private Integer toDayPv;

    /**
     * 历史UV
     */
    private Integer totalUv;

    /**
     * 今日UV
     */
    private Integer toDayUv;

    /**
     * 历史UIP
     */
    private Integer totalUIp;

    /**
     * 今日UIP
     */
    private Integer toDayUIp;
}
