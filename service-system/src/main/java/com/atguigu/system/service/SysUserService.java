package com.atguigu.system.service;

import com.atguigu.model.system.SysUser;
import com.atguigu.model.vo.SysUserQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-05-09
 */
public interface SysUserService extends IService<SysUser> {
    //IPage<SysUser> selectPage(Page<SysUser> pageParam, SysUserQueryVo adminQueryVo);

/*    //根据用户id获取用户登录信息
    Map<String, Object> getUserInfoByUserId(Long userId);

    //根据用户id获取用户按钮权限标识符
    List<String> getUserBtnPermsByUserId(Long id);*/

    //更新用户状态
    //正常 和 禁用
    void updateUserStatus(Long id, Integer status);

    SysUser getUserInfoByUserName(String username);

    Map<String, Object> getUserPermsInfoByUserId(Long userId);
}

