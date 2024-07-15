package com.yupc.myshortlink.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupc.myshortlink.admin.common.convention.result.Result;
import com.yupc.myshortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.yupc.myshortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

public interface RecycleBinService {

    /**
     * 回收站分页查询
     */
    Result<Page<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam);
}
