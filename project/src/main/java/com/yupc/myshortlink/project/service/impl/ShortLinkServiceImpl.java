package com.yupc.myshortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupc.myshortlink.project.common.convention.exception.ClientException;
import com.yupc.myshortlink.project.common.convention.exception.ServiceException;
import com.yupc.myshortlink.project.common.enums.VailDateTimeTypeEnum;
import com.yupc.myshortlink.project.dao.entity.*;
import com.yupc.myshortlink.project.dao.mapper.*;
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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.yupc.myshortlink.project.common.constant.RedisKeyConst.*;
import static com.yupc.myshortlink.project.common.constant.ShortLinkConstant.AMAP_REMOTE_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortUrlCreateCachePenetrationBloomFilter;
    private final ShortLinkGoToMapper shortLinkGoToMapper;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkOSStatsMapper linkOSStatsMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${short-link.stats.locale.amap-key}")
    String amapKey;

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
                .favicon(getFaviconByURL(requestParam.getOriginUrl()))
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
                .set(String.format(GOTO_SHORT_LINK_KEY, fullShortLink), requestParam.getOriginUrl(), LinkUtil.getLinkCacheValidDate(requestParam.getValidTime()), TimeUnit.MILLISECONDS);
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
            shortLinkStats(fullShortUrl, serverHttpRequest, serverHttpResponse);
            ((HttpServletResponse) serverHttpResponse).sendRedirect(originalLink);
            return;
        }

        boolean containsed = shortUrlCreateCachePenetrationBloomFilter.contains(fullShortUrl);
        if (!containsed) {
            ((HttpServletResponse) serverHttpResponse).sendRedirect("/page/notfound");
            return;
        }
        String gotoIsNullShortLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(gotoIsNullShortLink)) {
            ((HttpServletResponse) serverHttpResponse).sendRedirect("/page/notfound");
            return;
        }

        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
        lock.lock();
        try {
            originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
            if (StrUtil.isNotBlank(originalLink)) {
                shortLinkStats(fullShortUrl, serverHttpRequest, serverHttpResponse);
                ((HttpServletResponse) serverHttpResponse).sendRedirect(originalLink);
                return;
            }
            LambdaQueryWrapper<ShortLinkGoToDO> linkGoToDOLambdaQueryWrapper = Wrappers.lambdaQuery(ShortLinkGoToDO.class)
                    .eq(ShortLinkGoToDO::getFullShortUrl, fullShortUrl);
            ShortLinkGoToDO shortLinkGoToDO = shortLinkGoToMapper.selectOne(linkGoToDOLambdaQueryWrapper);
            if (shortLinkGoToDO == null) {
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl), "-", 30, TimeUnit.MINUTES);

                ((HttpServletResponse) serverHttpResponse).sendRedirect("/page/notfound");

                //风控
                return;
            }
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkGoToDO.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl);
            ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);
            if (hasShortLinkDO == null || hasShortLinkDO.getValidTime().before(new Date())) {
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl), "-", 30, TimeUnit.MINUTES);
                ((HttpServletResponse) serverHttpResponse).sendRedirect("/page/notfound");
                return;

            }
            shortLinkStats(fullShortUrl, serverHttpRequest, serverHttpResponse);
            stringRedisTemplate.opsForValue().set(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl), hasShortLinkDO.getOriginUrl());
            ((HttpServletResponse) serverHttpResponse).sendRedirect(hasShortLinkDO.getOriginUrl());
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

    private String getFaviconByURL(String url) {
        // Fetch the HTML content from the website
        String htmlContent = restTemplate.getForObject(url, String.class);

        // Parse the HTML content using Jsoup
        Document doc = Jsoup.parse(htmlContent, url);

        // Look for the favicon link elements
        Element iconElement = doc.selectFirst("link[rel~=(?i)^(shortcut icon|icon)]");

        // If found, return the absolute URL of the favicon
        if (iconElement != null) {
            String faviconUrl = iconElement.absUrl("href");
            return faviconUrl;
        }
        return null;
    }

    public static String getOS(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.toLowerCase().contains("windows")) {
            return "Windows";
        } else if (userAgent.toLowerCase().contains("mac")) {
            return "Mac OS";
        } else if (userAgent.toLowerCase().contains("linux")) {
            return "Linux";
        } else if (userAgent.toLowerCase().contains("unix")) {
            return "Unix";
        } else if (userAgent.toLowerCase().contains("android")) {
            return "Android";
        } else if (userAgent.toLowerCase().contains("iphone")) {
            return "iOS";
        } else {
            return "Unknown";
        }
    }

    private void shortLinkStats(String fullShortUrl, ServletRequest request, ServletResponse response) {
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        AtomicBoolean uvBoolean = new AtomicBoolean();
        Runnable addResponseCookie = () -> {
            String uv = UUID.fastUUID().toString();
            Cookie cookie = new Cookie("uv", uv);
            cookie.setMaxAge(60 * 60 * 24 * 30);
            cookie.setPath(StrUtil.sub(fullShortUrl, fullShortUrl.indexOf("/"), fullShortUrl.length()));
            ((HttpServletResponse) response).addCookie(cookie);
            uvBoolean.set(Boolean.TRUE);
            stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + fullShortUrl, uv);
        };
        try {
            if (ArrayUtil.isNotEmpty(cookies)) {
                Arrays.stream(cookies)
                        .filter(each -> Objects.equals(each.getName(), "uv"))
                        .findFirst()
                        .map(Cookie::getValue)
                        .ifPresentOrElse(
                                each -> {
                                    Long uvAdded = stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + fullShortUrl, each);
                                    uvBoolean.set(uvAdded != null && uvAdded > 0L);
                                }, addResponseCookie
                        );
            } else {
                addResponseCookie.run();
            }
            String remoteAddr = LinkUtil.getIp((HttpServletRequest) request);
            Long uipAdded = stringRedisTemplate.opsForSet().add("short-link:stats:uip:" + fullShortUrl, remoteAddr);
            boolean uipFirstFlag = uipAdded != null && uipAdded > 0L;
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);
            Date date = new Date();
            int hour = DateUtil.hour(date, true);
            Week week = DateUtil.dayOfWeekEnum(date);
            int value = week.getIso8601Value();
            LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                    .uv(uvBoolean.get() ? 1 : 0)
                    .uip(uipFirstFlag ? 1 : 0)
                    .pv(1)
                    .weekday(value)
                    .hour(hour)
                    .fullShortUrl(fullShortUrl)
                    .gid(shortLinkDO.getGid())
                    .date(date)
                    .build();
            Map<String, Object> localeParamMap = new HashMap<>();
            localeParamMap.put("key", amapKey);
            localeParamMap.put("ip", remoteAddr);
            String localResultStr = HttpUtil.get(AMAP_REMOTE_URL, localeParamMap);
            JSONObject jsonObject = JSON.parseObject(localResultStr);
            String infocode = jsonObject.getString("infocode");
            LinkLocaleStatsDO linkLocaleStatsDO;
            if (StrUtil.isNotBlank(infocode) && StrUtil.equals(infocode, "10000")) {
                String province = jsonObject.getString("province");
                boolean unKnowFlag = StrUtil.isBlank(province);
                linkLocaleStatsDO = LinkLocaleStatsDO.builder()
                        .fullShortUrl(fullShortUrl)
                        .gid(shortLinkDO.getGid())
                        .province(unKnowFlag ? "未知" : province)
                        .city(unKnowFlag ? "未知" : jsonObject.getString("city"))
                        .adcode(unKnowFlag ? "未知" : jsonObject.getString("adcode"))
                        .cnt(1)
                        .country("中国")
                        .date(date)
                        .build();
                linkLocaleStatsMapper.ShortLinkLocaleStats(linkLocaleStatsDO);
                LinkOSStatsDO osStatsDO = LinkOSStatsDO.builder()
                        .gid(shortLinkDO.getGid())
                        .os(getOS((HttpServletRequest)request))
                        .cnt(1)
                        .date(date)
                        .fullShortUrl(fullShortUrl)
                        .build();
                linkOSStatsMapper.ShortLinkOSStats(osStatsDO);
            }

            linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);
        } catch (Exception ex) {
            log.info("短链接统计异常", ex);
        }
    }

}
