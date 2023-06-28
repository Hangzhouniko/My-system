package com.cqq.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.cqq.reggie.common.BaseContext;
import com.cqq.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
//        log.info("拦截到请求:{}",requestURI);


        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

//        2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

//        3.如果不需要处理即可放行
        if(check){
//            log.info("本次请求{}不需要处理，放行",requestURI);
            filterChain.doFilter(request, response);
            return;
        }

//        4.判断登录状态,如果已登录,则直接放行
        if(request.getSession().getAttribute("employee")!=null){
//            log.info("用户已登录,用户ID为:{}",request.getSession().getAttribute("employee"));
            Long id= (long) request.getSession().getAttribute("employee");

            BaseContext.threadLocal.set(id);
//            log.info("BaseContext.threadLocal.ID:{}",BaseContext.threadLocal.get());


//            long id2 = Thread.currentThread().getId();
//            log.info("线程ID为：{}",id);

            filterChain.doFilter(request, response);
            return;
        }

        //        4-1.判断移动端登录状态,如果已登录,则直接放行
        if(request.getSession().getAttribute("user")!=null){
//            log.info("用户已登录,用户ID为:{}",request.getSession().getAttribute("employee"));
            Long id= (long) request.getSession().getAttribute("user");

            BaseContext.threadLocal.set(id);
//            log.info("BaseContext.threadLocal.ID:{}",BaseContext.threadLocal.get());


//            long id2 = Thread.currentThread().getId();
//            log.info("线程ID为：{}",id);

            filterChain.doFilter(request, response);
            return;
        }

        log.error("未登录！！！！！！！！！");

//        5.如果未登录则跳转登录
//        log.info("用户未登录，跳转登录页面！");
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));






//
//        log.info("拦截到请求：{}", request.getRequestURI());
//        log.info("拦截到请求：{}", request.getRequestURL());


//        filterChain.doFilter(request, response);

    }

    /**
     * 路径匹配
     *
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
