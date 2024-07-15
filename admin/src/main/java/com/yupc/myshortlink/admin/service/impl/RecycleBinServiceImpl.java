package com.yupc.myshortlink.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupc.myshortlink.admin.common.biz.user.UserContext;
import com.yupc.myshortlink.admin.common.convention.exception.ServiceException;
import com.yupc.myshortlink.admin.common.convention.result.Result;
import com.yupc.myshortlink.admin.dao.entity.GroupDO;
import com.yupc.myshortlink.admin.dao.mapper.GroupMapper;
import com.yupc.myshortlink.admin.remote.dto.ShortLinkActualRemoteService;
import com.yupc.myshortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.yupc.myshortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.yupc.myshortlink.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements RecycleBinService {

    private final GroupMapper groupMapper;
    private final ShortLinkActualRemoteService shortLinkActualRemoteService;


    @Override
    public Result<Page<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> groupDOList = groupMapper.selectList(queryWrapper);
            if (CollUtil.isEmpty(groupDOList)) {
            throw new ServiceException("用户无分组信息");
        }
        requestParam.setGidList(groupDOList.stream().map(GroupDO::getGid).toList());
        return shortLinkActualRemoteService.pageRecycleBinShortLink(requestParam.getGidList(), requestParam.getCurrent(), requestParam.getSize());
    }
}
