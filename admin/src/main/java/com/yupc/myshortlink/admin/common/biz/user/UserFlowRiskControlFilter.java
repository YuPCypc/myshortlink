package com.yupc.myshortlink.admin.common.biz.user;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.yupc.myshortlink.admin.common.convention.exception.ClientException;
import com.yupc.myshortlink.admin.common.convention.result.Results;
import com.yupc.myshortlink.admin.config.UserFlowRiskControlConfiguration;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import static com.yupc.myshortlink.admin.common.convention.errcode.BaseErrorCode.FLOW_LIMIT_ERROR;

/**
 * 用户操作流量风控过滤器
 */
@Slf4j
@RequiredArgsConstructor
public class UserFlowRiskControlFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;
    private final UserFlowRiskControlConfiguration userFlowRiskControlConfiguration;

    private static final String USER_FLOW_RISK_CONTROL_LUA_SCRIPT_PATH = "lua/user_flow_risk_control.lua";

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 创建一个DefaultRedisScript对象
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        // 设置Lua脚本的路径
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(USER_FLOW_RISK_CONTROL_LUA_SCRIPT_PATH)));
        // 设置返回结果的类型
        redisScript.setResultType(Long.class);

        // 获取用户名，如果为空则默认为"other"
        String username = Optional.ofNullable(UserContext.getUsername()).orElse("other");

        Long result = null;

        try {
            // 执行Lua脚本，并传入参数
            result = stringRedisTemplate.execute(redisScript,
                    Lists.newArrayList(username),
                    userFlowRiskControlConfiguration.getTimeWindow());
        } catch (Throwable ex) {
            // 执行Lua脚本出错时记录日志并返回错误结果
            log.error("执行用户请求流量限制LUA脚本出错", ex);
            // 将错误结果转换为JSON并返回给客户端
            returnJson((HttpServletResponse) response, JSON.toJSONString(Results.failure(new ClientException(FLOW_LIMIT_ERROR))));
        }

        // 如果result为空或者超过最大访问次数
        if (result == null || result > userFlowRiskControlConfiguration.getMaxAccessCount()) {
            // 返回错误结果给客户端
            returnJson((HttpServletResponse) response, JSON.toJSONString(Results.failure(new ClientException(FLOW_LIMIT_ERROR))));
        }

        // 继续执行过滤器链中的下一个过滤器
        filterChain.doFilter(request, response);
    }


    private void returnJson(HttpServletResponse response, String json) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(json);
        }
    }
}
