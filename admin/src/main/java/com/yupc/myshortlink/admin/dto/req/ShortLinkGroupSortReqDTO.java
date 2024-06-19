package com.yupc.myshortlink.admin.dto.req;

import lombok.Data;

/**
 * 短链接分组创建参数
 */
@Data
public class ShortLinkGroupSortReqDTO {
    /**
     * 分组ID
     */
    private String gid;

    /**
     * 分组序号
     */
    private Integer sortOrder;
}
