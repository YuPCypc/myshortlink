package com.yupc.myshortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupc.myshortlink.project.common.convention.exception.ClientException;
import com.yupc.myshortlink.project.common.convention.exception.ServiceException;
import com.yupc.myshortlink.project.common.enums.VailDateTimeTypeEnum;
import com.yupc.myshortlink.project.dao.entity.ShortLinkDO;
import com.yupc.myshortlink.project.dao.entity.ShortLinkGoToDO;
import com.yupc.myshortlink.project.dao.mapper.ShortLinkGoToMapper;
import com.yupc.myshortlink.project.dao.mapper.ShortLinkMapper;
import com.yupc.myshortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.yupc.myshortlink.project.dto.req.ShortLinkPageReqDTO;
import com.yupc.myshortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.yupc.myshortlink.project.dto.resp.ShortLinkCountQueryRespDTO;
import com.yupc.myshortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.yupc.myshortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.yupc.myshortlink.project.service.ShortLinkService;
import com.yupc.myshortlink.project.toolkit.HashUtil;
import com.yupc.myshortlink.project.toolkit.LinkUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.yupc.myshortlink.project.common.constant.RedisKeyConst.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortUrlCreateCachePenetrationBloomFilter;
    private final ShortLinkGoToMapper shortLinkGoToMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String generateSuffix = generateSuffix(requestParam);
        String fullShortLink = StrBuilder.create(requestParam.getDomain())
                .append("/")
                .append(generateSuffix)
                .toString();
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(requestParam.getDomain())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .createType(requestParam.getCreateType())
                .validDateType(requestParam.getValidDateType())
                .validTime(requestParam.getValidTime())
                .describe(requestParam.getDescribe())
                .shortUrl(generateSuffix)
                .enableStatus(0)
                .fullShortUrl(fullShortLink)
                .build();
        ShortLinkGoToDO shortLinkGoToDO = ShortLinkGoToDO.builder()
                .fullShortUrl(fullShortLink)
                .gid(requestParam.getGid())
                .build();
        try {
            baseMapper.insert(shortLinkDO);
            shortLinkGoToMapper.insert(shortLinkGoToDO);
        } catch (DuplicateKeyException ex) {
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, fullShortLink);
            ShortLinkDO shortLinkDO1 = baseMapper.selectOne(queryWrapper);
            if (shortLinkDO1 != null) {
                log.info("短链接：{} 入库", fullShortLink);
                throw new ServiceException("短链接生成重复");
            }
        }
        stringRedisTemplate.opsForValue()
                .set(String.format(GOTO_SHORT_LINK_KEY,fullShortLink),requestParam.getOriginUrl(), LinkUtil.getLinkCacheValidDate(requestParam.getValidTime()),TimeUnit.MILLISECONDS);
        shortUrlCreateCachePenetrationBloomFilter.add(fullShortLink);
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl("http://" + shortLinkDO.getFullShortUrl())
                .originUrl(shortLinkDO.getOriginUrl())
                .gid(shortLinkDO.getGid())
                .build();

    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0)
                .orderByDesc(ShortLinkDO::getCreateTime);
        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(requestParam, queryWrapper);
        return resultPage.convert(
                each -> {
                    ShortLinkPageRespDTO result = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
                    result.setDomain("http://" + result.getDomain());
                    return result;
                });
    }

    @Override
    public List<ShortLinkCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query(new ShortLinkDO())
                .select("gid as gid, count(1) as shortLinkCount")
                .in("gid", requestParam)
                .eq("enable_status", 0)
                .groupBy("gid");
        List<Map<String, Object>> shortLinkDOList = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(shortLinkDOList, ShortLinkCountQueryRespDTO.class);
    }

    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {

        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl());
        ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);
        if (hasShortLinkDO == null) {
            throw new ClientException("短链接不存在");
        }
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(hasShortLinkDO.getDomain())
                .shortUrl(hasShortLinkDO.getShortUrl())
                .clickNum(hasShortLinkDO.getClickNum())
                .favicon(hasShortLinkDO.getFavicon())
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .describe(requestParam.getDescribe())
                .validTime(requestParam.getValidTime())
                .validDateType(requestParam.getValidDateType())
                .build();
        if (Objects.equals(hasShortLinkDO.getGid(), requestParam.getGid())) {
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .set(Objects.equals(requestParam.getValidDateType(), VailDateTimeTypeEnum.PERMANENT.getType()), ShortLinkDO::getValidTime, null);
            baseMapper.update(shortLinkDO, updateWrapper);
        } else {
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, hasShortLinkDO.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            baseMapper.delete(updateWrapper);
            baseMapper.insert(shortLinkDO);
        }
    }

    @SneakyThrows
    @Override
    public void restoreUrl(String shortUrl, ServletRequest serverHttpRequest, ServletResponse serverHttpResponse) {
        String serverName = serverHttpRequest.getServerName();
        String fullShortUrl = serverName + "/" + shortUrl;
        String originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));

        if (StrUtil.isNotBlank(originalLink)) {
            ((HttpServletResponse) serverHttpResponse).sendRedirect(originalLink);
            return;
        }

        boolean containsed = shortUrlCreateCachePenetrationBloomFilter.contains(fullShortUrl);
        if (!containsed){
            return;
        }
        String gotoIsNullShortLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(gotoIsNullShortLink)){
            return;
        }

        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
        lock.lock();
        try {
            originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
            if (StrUtil.isNotBlank(originalLink)) {
                ((HttpServletResponse) serverHttpResponse).sendRedirect(originalLink);
                return;
            }
            LambdaQueryWrapper<ShortLinkGoToDO> linkGoToDOLambdaQueryWrapper = Wrappers.lambdaQuery(ShortLinkGoToDO.class)
                    .eq(ShortLinkGoToDO::getFullShortUrl, fullShortUrl);
            ShortLinkGoToDO shortLinkGoToDO = shortLinkGoToMapper.selectOne(linkGoToDOLambdaQueryWrapper);
            if (shortLinkGoToDO == null) {
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl),"-",30, TimeUnit.MINUTES);

                //风控
                return;
            }
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkGoToDO.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl);
            ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);
            if (hasShortLinkDO != null) {
                if (hasShortLinkDO.getValidTime()!=null&&hasShortLinkDO.getValidTime().before(new Date())){
                    stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl),"-",30, TimeUnit.MINUTES);
                    return;
                }
                stringRedisTemplate.opsForValue().set(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl), hasShortLinkDO.getOriginUrl());
                ((HttpServletResponse) serverHttpResponse).sendRedirect(hasShortLinkDO.getOriginUrl());
            }
        } finally {
            lock.unlock();
        }
    }

    private String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        String originUrl = requestParam.getOriginUrl();
        int customGenerateCount = 10;
        while (true) {
            if (customGenerateCount <= 0) {
                throw new ServiceException("短链接生成频繁，请稍后再试");
            }
            originUrl += System.currentTimeMillis();
            String shortUrl = HashUtil.hashToBase62(originUrl);

            if (!shortUrlCreateCachePenetrationBloomFilter.contains(requestParam.getDomain() + "/" + shortUrl)) {
                return shortUrl;
            }
            customGenerateCount--;
        }
    }

}
