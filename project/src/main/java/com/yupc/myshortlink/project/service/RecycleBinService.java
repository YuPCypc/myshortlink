package com.yupc.myshortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yupc.myshortlink.project.dto.req.RecycleBinRecoverReqDTO;
import com.yupc.myshortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.yupc.myshortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.yupc.myshortlink.project.dto.resp.ShortLinkPageRespDTO;

/**
 * 回收站接口管理层
 */
public interface RecycleBinService {
    /**
     * 移至回收站功能
     */
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);

    /**
     * 分页查询回收站功能
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam);

    void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam);
}
