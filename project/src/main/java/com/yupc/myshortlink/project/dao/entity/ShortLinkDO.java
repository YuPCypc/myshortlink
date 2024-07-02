package com.yupc.myshortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yupc.myshortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@TableName(value = "t_link")
public class ShortLinkDO extends BaseDO {

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
     * 启用标识：0:未启用n1:启用
     */
    private int enableStatus;

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
     * 网站图标
     */
    private String favicon;

    /**
     * 历史PV
     */
    @TableField(exist = false)
    private Integer totalPv;

    /**
     * 历史UV
     */
    @TableField(exist = false)
    private Integer totalUv;

    /**
     * 历史UIP
     */
    @TableField(exist = false)
    private Integer totalUip;
}
