package com.yupc.myshortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupc.myshortlink.project.dao.entity.LinkOSStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * 短链接OS访问监控持久层
 */
public interface LinkOSStatsMapper extends BaseMapper<LinkOSStatsDO> {
    @Insert("INSERT INTO " +
            "t_link_os_stats (full_short_url, gid,date,cnt,os,create_time, update_time, del_flag) " +
            "VALUES( #{linkOSStats.fullShortUrl}, #{linkOSStats.gid}, #{linkOSStats.date}, #{linkOSStats.cnt}," +
            "#{linkOSStats.os},NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkOSStats.cnt};")
    void ShortLinkOSStats(@Param("linkOSStats") LinkOSStatsDO linkOSStatsDO);
}
