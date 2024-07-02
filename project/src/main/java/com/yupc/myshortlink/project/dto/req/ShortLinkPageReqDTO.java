package com.yupc.myshortlink.project.dto.req;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupc.myshortlink.project.dao.entity.ShortLinkDO;
import lombok.Data;

/**
 * 短链接分页请求标识
 */
@Data
public class ShortLinkPageReqDTO extends Page<ShortLinkDO> {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 排序标识
     */
    private String orderTag;
}
