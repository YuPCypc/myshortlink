package com.yupc.myshortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupc.myshortlink.admin.dao.entity.GroupDO;
import com.yupc.myshortlink.admin.dto.req.ShortLinkGroupDeleteReqDTO;
import com.yupc.myshortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.yupc.myshortlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

/**
 * 短链接分组接口层
 */
public interface GroupService extends IService<GroupDO> {
    /**
     * 新增短链接分组
     *
     * @param groupName 短链接分组名
     */
    void saveGroup(String groupName);

    /**
     * 查询短链接分组集合
     * @return 短链接分组集合
     */
    List<ShortLinkGroupRespDTO> listGroup();

    /**
     * 修改短链接分组
     * @param requestParam 短链接修改分组请求参数
     */
    void updateGroup(ShortLinkGroupUpdateReqDTO requestParam);

    /**
     * 删除短链接分组
     * @param requestParam 短链接删除分组请求参数
     */
    void deleteGroup(ShortLinkGroupDeleteReqDTO requestParam);
}
