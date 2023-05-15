package com.atguigu.system.filter;

import com.atguigu.common.result.Result;
import com.atguigu.common.result.ResultCodeEnum;
import com.atguigu.common.util.ResponseUtil;
import com.atguigu.model.system.SysUser;
import com.atguigu.system.custom.CustomUser;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private RedisTemplate redisTemplate;
    public TokenAuthenticationFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //1 判断当前请求路径是否登录路径，如果是放行
        String requestURI = request.getRequestURI();
        System.out.println("requestURI:: "+requestURI);
        if("/admin/system/index/login".equals(requestURI)) {
            //放行
            filterChain.doFilter(request,response);
            return;
        }

        //2 从请求头获取token，根据token查询redis，CustomUser转换Security里面的UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        //返回对象如果不为空，登录，上下文件对象
        if(authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //放行
            filterChain.doFilter(request, response);
        } else {
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.PERMISSION));
        }
    }

    //2 从请求头获取token，根据token查询redis，
    // CustomUser转换Security里面的UsernamePasswordAuthenticationToken
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("token");
        if(token != null) {
            //根据token查询redis
            SysUser sysUser = (SysUser)redisTemplate.boundValueOps(token).get();
            if(sysUser != null) {
                //获取用户权限数据
                List<String> userPermsList = sysUser.getUserPermsList();

                if(userPermsList != null && userPermsList.size()>0) {
                    //封装权限数据，返回

                    //List<String> == List<SimpleGrantedAuthority>
                    List<SimpleGrantedAuthority> authorityList =
                            userPermsList.stream()
                                    .filter(code -> !StringUtils.isEmpty(code.trim()))
                                    .map(code -> new SimpleGrantedAuthority(code.trim()))
                                    .collect(Collectors.toList());

                    return new
                            UsernamePasswordAuthenticationToken(sysUser.getUsername(),
                            null, authorityList);
                } else {
                    return new
                            UsernamePasswordAuthenticationToken(sysUser.getUsername(),
                            null, Collections.emptyList());
                }
            }
        }
        return null;
    }
}
