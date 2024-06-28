package com.yupc.myshortlink.project.toolkit;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.Optional;

import static com.yupc.myshortlink.project.common.constant.ShortLinkConstant.DEFAULT_CACHE_TIMEOUT;

public class LinkUtil {

    /**
     * 获取短链接缓存
     */
    public static Long getLinkCacheValidDate(Date ValidTime) {
        return Optional.ofNullable(ValidTime)
                .map(each -> DateUtil.between(new Date(), each, DateUnit.MS))
                .orElse(DEFAULT_CACHE_TIMEOUT);
    }
}
