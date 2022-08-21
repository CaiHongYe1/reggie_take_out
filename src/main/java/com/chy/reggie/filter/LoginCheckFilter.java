package com.chy.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.chy.reggie.common.BaseContext;
import com.chy.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@Slf4j
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
//  路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        log.info("拦截到请求： {}",request.getRequestURI());

//        获取本次请求的URI
        String requestURI = request.getRequestURI();

//        判断本次请求是否需要处理
        //要放行的请求： 登录登出请求、静态资源
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg", //移动端发送短信
                "/user/login" //移动端登录
        };

        boolean check = check(urls, requestURI);

//        如果不需要处理 直接放行
        if(check){
            filterChain.doFilter(request,response);
//            log.info("本次请求{}不需要处理",request.getRequestURI());
            return;
        }

//       4-1员工 如果需要处理，如果已经登录 则放行
        if(request.getSession().getAttribute("employee") != null){
//            将登录员工的ID取出存入ThreadLocal中
            BaseContext.setCurrentId((Long)request.getSession().getAttribute("employee"));
//            log.info("用户已经登录，用户ID为：{}",request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }

        //4-2 移动端用户登录        如果需要处理，如果已经登录 则放行
        if(request.getSession().getAttribute("user") != null){
//            将登录员工的ID取出存入ThreadLocal中
            BaseContext.setCurrentId((Long)request.getSession().getAttribute("user"));
//            log.info("用户已经登录，用户ID为：{}",request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }

//        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url,requestURI);
            if(match){
                //本次请求的URI和需要放行的URI匹配上了 返回true 放行
                return true;
            }
        }
        //本次请求的URI和需要放行的URI匹配不上了 返回false 不放行
        return false;
    }
}
