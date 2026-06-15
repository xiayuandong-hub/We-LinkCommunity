package co.yiiu.welink.interceptor;

import co.yiiu.welink.model.User;
import co.yiiu.welink.service.ISystemConfigService;
import co.yiiu.welink.service.IUserService;
import co.yiiu.welink.util.CookieUtil;
import co.yiiu.welink.util.HttpUtil;
import co.yiiu.welink.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by We-Link.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
@Component
public class CommonInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(CommonInterceptor.class);
    @Resource
    private IUserService userService;
    @Resource
    private CookieUtil cookieUtil;
    @Resource
    private ISystemConfigService systemConfigService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long start = System.currentTimeMillis();
        request.setAttribute("_start", start);

        // 判断session里有用户信息，有直接通过
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("_user");
        if (user == null) {
            // 获取cookie里的token，查询用户的信息并放入session里
            Object cookieName = systemConfigService.selectAllConfig().get("cookie_name");
            String token = cookieName != null ? cookieUtil.getCookie(cookieName.toString()) : null;
            if (!StringUtils.isEmpty(token)) {
                // 根据token查询用户是否存在
                user = userService.selectByToken(token);
                if (user != null) {
                    // 用户存在写session，cookie然后给予通过
                    session.setAttribute("_user", user);
                    Object name = systemConfigService.selectAllConfig().get("cookie_name");
                    if (name != null) {
                        cookieUtil.setCookie(name.toString(), user.getToken());
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (!HttpUtil.isApiRequest(request) && modelAndView != null) {
            try {
                modelAndView.addObject("site", systemConfigService.selectAllConfigWithoutPassword());
            } catch (Exception e) {
                log.error("Failed to load site config: {}", e.getMessage());
                modelAndView.addObject("site", new java.util.HashMap<String, String>());
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long start = (long) request.getAttribute("_start");
        String actionName = request.getRequestURI();
        String clientIp = IpUtil.getIpAddr(request);
        StringBuilder logString = new StringBuilder();
        logString.append(clientIp).append("|").append(actionName).append("|");
        Map<String, String[]> params = request.getParameterMap();
        params.forEach((key, value) -> {
            logString.append(key);
            logString.append("=");
            for (String paramString : value) {
                logString.append(paramString);
            }
            logString.append("|");
        });
        long executionTime = System.currentTimeMillis() - start;
        logString.append("excitation=").append(executionTime).append("ms");
        log.info(logString.toString());
    }
}
