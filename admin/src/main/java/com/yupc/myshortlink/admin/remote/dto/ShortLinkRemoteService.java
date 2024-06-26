package com.yupc.myshortlink.admin.remote.dto;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yupc.myshortlink.admin.common.convention.result.Result;
import com.yupc.myshortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.yupc.myshortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.yupc.myshortlink.admin.remote.dto.resp.ShortLinkCountQueryRespDTO;
import com.yupc.myshortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.yupc.myshortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
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
}
