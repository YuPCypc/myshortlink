package com.yupc.myshortlink.project.dto.resp;

import lombok.Data;

/**
 * 短链接分组查询返回参数
 */
@Data
public class ShortLinkCountQueryRespDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组查询短链接
     */
    private Integer shortLinkCount;
}
