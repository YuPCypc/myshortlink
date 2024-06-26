package com.yupc.myshortlink.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupc.myshortlink.project.dao.entity.ShortLinkDO;
import com.yupc.myshortlink.project.dao.mapper.ShortLinkMapper;
import com.yupc.myshortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.yupc.myshortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.yupc.myshortlink.project.common.constant.RedisKeyConst.GOTO_SHORT_LINK_KEY;

/**
 * 回收站接口实现层
 */
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl extends ServiceImpl<ShortLinkMapper,ShortLinkDO> implements RecycleBinService{

    private final StringRedisTemplate stringRedisTemplate;
    @Override
    public void saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortLink())
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO shortLinkDO = ShortLinkDO.builder().enableStatus(1).build();
        baseMapper.update(shortLinkDO,updateWrapper);
        stringRedisTemplate.delete(String.format(GOTO_SHORT_LINK_KEY, requestParam.getFullShortLink()));
    }


}
