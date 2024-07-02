package com.yupc.myshortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yupc.myshortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 短链接OS访问监控实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_link_access_stats")
public class LinkOSStatsDO extends BaseDO {

    /**
     * id
     */
    private Long id;

    /**
     * gid
     */
    private String gid;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 日期
     */
    private Date date;

    /**
     * 访问量
     */
    private Integer cnt;


    /**
     * os
     */
    private String os;
}