package com.yupc.myshortlink.project.service;

import com.yupc.myshortlink.project.dto.req.RecycleBinSaveReqDTO;

/**
 * 回收站接口管理层
 */
public interface RecycleBinService {
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);

}
