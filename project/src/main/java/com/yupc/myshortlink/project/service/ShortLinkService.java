package com.yupc.myshortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupc.myshortlink.project.dao.entity.ShortLinkDO;
import com.yupc.myshortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.yupc.myshortlink.project.dto.resp.ShortLinkCreateRespDTO;

/**
 * 短链接接口层
 */
public interface ShortLinkService extends IService<ShortLinkDO> {

    /**
     * 创建新的短链接
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);
}
