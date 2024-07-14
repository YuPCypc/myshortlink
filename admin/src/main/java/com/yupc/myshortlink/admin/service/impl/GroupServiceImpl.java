package com.yupc.myshortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupc.myshortlink.admin.common.biz.user.UserContext;
import com.yupc.myshortlink.admin.common.convention.exception.ClientException;
import com.yupc.myshortlink.admin.common.convention.result.Result;
import com.yupc.myshortlink.admin.dao.entity.GroupDO;
import com.yupc.myshortlink.admin.dao.mapper.GroupMapper;
import com.yupc.myshortlink.admin.dto.req.ShortLinkGroupDeleteReqDTO;
import com.yupc.myshortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.yupc.myshortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.yupc.myshortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.yupc.myshortlink.admin.remote.dto.ShortLinkRemoteService;
import com.yupc.myshortlink.admin.remote.dto.resp.ShortLinkCountQueryRespDTO;
import com.yupc.myshortlink.admin.service.GroupService;
import com.yupc.myshortlink.admin.toolkit.RandomNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.yupc.myshortlink.admin.common.constant.RedisCacheConstant.LOCK_GROUP_CREATE_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    private final RedissonClient redissonClient;
    @Value("${short-link.group.max-num}")
    private Integer groupMaxNum;

    /**
     * 后续spring-cloud Feign调用
     */
    ShortLinkRemoteService shortLinkRemoteService=new ShortLinkRemoteService(){

    };



    @Override
    public void saveGroup(String groupName) {
        this.saveGroup(UserContext.getUsername(),groupName);
    }
    @Override
    public void saveGroup(String username,String groupName) {
        RLock lock = redissonClient.getLock(String.format(LOCK_GROUP_CREATE_KEY, username));
        lock.lock();
        try {
            LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                    .eq(GroupDO::getUsername, username)
                    .eq(GroupDO::getDelFlag, 0);
            List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
            if (CollUtil.isNotEmpty(groupDOList) && groupDOList.size() == groupMaxNum) {
                throw new ClientException(String.format("已超出最大分组数：%d", groupMaxNum));
            }
            String gid;
            do {
                gid = RandomNumberGenerator.generateRandomString();
            } while (!hasGid(username, gid));
            GroupDO groupDO = GroupDO.builder()
                    .gid(gid)
                    .sortOrder(0)
                    .username(username)
                    .name(groupName)
                    .build();
            baseMapper.insert(groupDO);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        // TODO 获取用户名
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOS = baseMapper.selectList(queryWrapper);
        Result<List<ShortLinkCountQueryRespDTO>> listResult = shortLinkRemoteService.countShortLink(groupDOS.stream().map(GroupDO::getGid).toList());
        List<ShortLinkGroupRespDTO> shortLinkGroupRespDTOS = BeanUtil.copyToList(groupDOS, ShortLinkGroupRespDTO.class);
        shortLinkGroupRespDTOS.forEach(each -> {
            String gid = each.getGid();
            Optional<ShortLinkCountQueryRespDTO> first = listResult.getData().stream()
                    .filter(item -> Objects.equals(item.getGid(), gid)).findFirst();
            first.ifPresent(item -> each.setShortLinkCount(first.get().getShortLinkCount()));
        });
        return shortLinkGroupRespDTOS;
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO requestParam) {
        LambdaQueryWrapper<GroupDO> updateWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setName(requestParam.getName());
        baseMapper.update(groupDO,updateWrapper);
    }

    @Override
    public void deleteGroup(ShortLinkGroupDeleteReqDTO requestParam) {
        LambdaQueryWrapper<GroupDO> updateWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO,updateWrapper);
    }

    @Override
    public void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam) {
        requestParam.forEach(each -> {
            GroupDO groupDO = GroupDO.builder()
                    .sortOrder(each.getSortOrder())
                    .build();
            LambdaQueryWrapper<GroupDO> updateWrapper = Wrappers.lambdaQuery(GroupDO.class)
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getGid, each.getGid())
                    .eq(GroupDO::getDelFlag, 0);
            baseMapper.update(groupDO,updateWrapper);
        });
    }

    private Boolean hasGid(String username,String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, username);
        GroupDO hasGroupDO = baseMapper.selectOne(queryWrapper);
        return hasGroupDO == null;
    }
}
