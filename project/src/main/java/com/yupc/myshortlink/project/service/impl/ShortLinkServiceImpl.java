package com.yupc.myshortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupc.myshortlink.project.common.convention.exception.ServiceException;
import com.yupc.myshortlink.project.dao.entity.ShortLinkDO;
import com.yupc.myshortlink.project.dao.mapper.ShortLinkMapper;
import com.yupc.myshortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.yupc.myshortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.yupc.myshortlink.project.service.ShortLinkService;
import com.yupc.myshortlink.project.toolkit.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortUrlCreateCachePenetrationBloomFilter;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String generateSuffix = generateSuffix(requestParam);
        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setShortUrl(generateSuffix);
        shortLinkDO.setEnableStatus(0);
        String fullShortLink = requestParam.getDomain() + "/" + generateSuffix;
        shortLinkDO.setFullShortUrl(fullShortLink);
        try{
            baseMapper.insert(shortLinkDO);
        }catch (DuplicateKeyException ex){
            log.info("短链接：{} 入库",fullShortLink);
            throw new ServiceException("短链接生成重复");
        };
        shortUrlCreateCachePenetrationBloomFilter.add(fullShortLink);
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .originUrl(shortLinkDO.getOriginUrl())
                .gid(shortLinkDO.getGid())
                .build();

    }

    private String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        String originUrl = requestParam.getOriginUrl();
        int customGenerateCount = 10;
        while (true) {
            if (customGenerateCount <= 0) {
                throw new ServiceException("短链接生成频繁，请稍后再试");
            }
            String shortUrl = HashUtil.hashToBase62(originUrl);

            if (!shortUrlCreateCachePenetrationBloomFilter.contains(requestParam.getDomain() + "/" + shortUrl)) {
                return shortUrl;
            }
            customGenerateCount--;
        }
    }

}
