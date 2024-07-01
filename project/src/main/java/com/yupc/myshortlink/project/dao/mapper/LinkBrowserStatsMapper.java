package com.yupc.myshortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupc.myshortlink.project.dao.entity.LinkBrowserStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface LinkBrowserStatsMapper extends BaseMapper<LinkBrowserStatsDO> {
    @Insert("INSERT INTO " +
            "t_link_browser_stats (full_short_url, gid,date,cnt,browser,create_time, update_time, del_flag) " +
            "VALUES( #{linkBrowserStats.fullShortUrl}, #{linkBrowserStats.gid}, #{linkBrowserStats.date}, #{linkBrowserStats.cnt}," +
            "#{linkBrowserStats.browser},NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkBrowserStats.cnt};")
    void ShortLinkBrowserStats(@Param("linkBrowserStats") LinkBrowserStatsDO linkBrowserStatsDO);
}
