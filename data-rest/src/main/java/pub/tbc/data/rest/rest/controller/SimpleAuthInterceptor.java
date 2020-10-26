package pub.tbc.data.rest.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author tbc by 2020/7/24 10:50 上午
 */
@Slf4j
// @Component
public class SimpleAuthInterceptor implements HandlerInterceptor, WebMvcConfigurer {

    private final String AUTH_HEAD_NAME = "M_AUTH";

    @Value("${task.manage.auth:123456")
    private String auth;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getRequestURI().contains("manage")) {
            String auth = request.getHeader(AUTH_HEAD_NAME);
            if (StringUtils.isEmpty(auth) || !auth.equalsIgnoreCase(this.auth)) {
                log.error("auth 验证不通过：{}", auth);
                return false;
            }
            log.info("auth 验证通过，ip: {}", request.getRemoteAddr());
        }
        return true;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns("/**");
    }
}
