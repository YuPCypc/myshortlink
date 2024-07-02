package com.yupc.myshortlink.admin.remote.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yupc.myshortlink.admin.common.convention.result.Result;
import com.yupc.myshortlink.admin.dto.req.RecycleBinRecoverReqDTO;
import com.yupc.myshortlink.admin.dto.req.RecycleBinRemoveReqDTO;
import com.yupc.myshortlink.admin.dto.req.RecycleBinSaveReqDTO;
import com.yupc.myshortlink.admin.remote.dto.req.*;
import com.yupc.myshortlink.admin.remote.dto.resp.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短链接中台远程调用服务
 */
public interface ShortLinkRemoteService {
    /**
     * 创建短链接
     */
    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam){
        String resultBodyStr = HttpUtil.post("http://127.0.0.1:8001/api/myshortlink/v1/create", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<Result<ShortLinkCreateRespDTO>>() {
        });
    }

    /**
     * 短链接分页查询
     */
    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", requestParam.getGid());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String response = HttpUtil.get("http://127.0.0.1:8001/api/myshortlink/v1/page", requestMap);
        return JSON.parseObject(response, new TypeReference<Result<IPage<ShortLinkPageRespDTO>>>() {

        });
    }

    /**
     * 查询短链接分组内的数量
     */
    default Result<List<ShortLinkCountQueryRespDTO>> countShortLink(@RequestParam("") List<String> requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("requestParam", requestParam);
        String response = HttpUtil.get("http://127.0.0.1:8001/api/myshortlink/v1/count", requestMap);
        return JSON.parseObject(response, new TypeReference<Result<List<ShortLinkCountQueryRespDTO>>>() {
        });
    }

    /**
     * 修改短链接
     */
    default void updateShortLink(ShortLinkUpdateReqDTO requestParam){
        HttpUtil.post("http://127.0.0.1:8001/api/myshortlink/v1/update", JSON.toJSONString(requestParam));
    }

    /**
     * 通过URL获取标题
     */
    default Result<String> getTitleByUrl(@RequestParam("url") String url){
        String responseData = HttpUtil.get(String.format("http://127.0.0.1:8001/api/myshortlink/v1/title?url=%s", url));
        return JSON.parseObject(responseData, new TypeReference<Result<String>>() {
        });
    }

    default void saveRecycleBin(RecycleBinSaveReqDTO requestParam){
           HttpUtil.post("http://127.0.0.1:8001/api/myshortlink/v1/recycle-bin",JSON.toJSONString(requestParam));
    }

    default Result<IPage<ShortLinkPageRespDTO>> RecycleBinpageShortLink(ShortLinkRecycleBinPageReqDTO requestParam){
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gidList",requestParam.getGidList());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String response = HttpUtil.get("http://127.0.0.1:8001/api/myshortlink/v1/recycle-bin/page", requestMap);
        return JSON.parseObject(response, new TypeReference<Result<IPage<ShortLinkPageRespDTO>>>() {

        });
    }

    default void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam){
       HttpUtil.post("http://127.0.0.1:8001/api/myshortlink/v1/recycle-bin/recover",JSON.toJSONString(requestParam));
    }

    default void removeRecycleBin(RecycleBinRemoveReqDTO requestParam) {
        HttpUtil.post("http://127.0.0.1:8001/api/myshortlink/v1/recycle-bin/remove",JSON.toJSONString(requestParam));
    }

    default Result<ShortLinkStatsRespDTO> oneShortLinkStats(ShortLinkStatsReqDTO requestParam){
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/myshortlink/v1/stats", BeanUtil.beanToMap(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }

    /**
     * 访问单个短链接指定时间内监控访问记录数据
     *
     * @param requestParam 访问短链接监控访问记录请求参数
     * @return 短链接监控访问记录信息
     */
    default Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(requestParam, false, true);
        stringObjectMap.remove("orders");
        stringObjectMap.remove("records");
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/myshortlink/v1/stats/access-record", stringObjectMap);
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }
}

