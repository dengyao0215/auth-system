package com.atguigu.system.service.impl;

import com.atguigu.model.system.SysMenu;
import com.atguigu.model.system.SysUser;
import com.atguigu.model.vo.RouterVo;
import com.atguigu.model.vo.SysUserQueryVo;
import com.atguigu.system.mapper.SysMenuMapper;
import com.atguigu.system.mapper.SysUserMapper;
import com.atguigu.system.service.SysUserService;
import com.atguigu.system.utils.MenuHelper;
import com.atguigu.system.utils.RouterHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-05-09
 */
@Transactional
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

 /*   @Autowired
    private SysUserService sysUserService;*/

/*    @Autowired
    private SysUserMapper sysUserMapper;*/

    @Autowired
    private SysMenuMapper sysMenuMapper;

    /**
     * 修改用户状态
     * @param id
     * @param status
     */
    @Override
    public void updateUserStatus(Long id, Integer status) {
        //根据userid查询
        SysUser sysUser = this.getById(id);

        //设置修改状态值
        sysUser.setStatus(status);

        //调用mapper方法修改
        baseMapper.updateById(sysUser);

    }

    @Override
    public SysUser getUserInfoByUserName(String username) {
        return baseMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    @Override
    public Map<String, Object> getUserPermsInfoByUserId(Long userId) {
        //根据userId查询用户菜单
        List<SysMenu> routerInfoByUserId = this.getRouterInfoByUserId(userId);

        List<SysMenu> sysMenuList = MenuHelper.buildTree(routerInfoByUserId);

        List<RouterVo> routerVos = RouterHelper.buildRouters(sysMenuList);

        //获取操作按钮数据
        List<String> permsList = this.getPermsByUserId(routerInfoByUserId);

        SysUser sysUser = baseMapper.selectById(userId);


        Map<String, Object> map = new HashMap<>();
        map.put("name", sysUser.getName());
        //当前权限控制使用不到，我们暂时忽略
        map.put("roles", new HashSet<>());
        map.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        map.put("routers", routerVos);//可以操作菜单数据
        map.put("buttons", permsList); //可以操作按钮数据
        return map;

    }

    //2 获取操作按钮数据
    public List<String> getPermsByUserId(List<SysMenu> menuList) {
        List<String> permsList = new ArrayList<>();
        for (SysMenu sysMenu : menuList) {
            if (sysMenu.getType() == 2) {
                String perms = sysMenu.getPerms();
                permsList.add(perms);
            }
        }
        return permsList;
    }

    public List<SysMenu> getRouterInfoByUserId(Long userId) {
        List<SysMenu> sysMenuList = null;

        if (userId == 1L) {
            sysMenuList = sysMenuMapper.selectList(null);
        } else {
            sysMenuList = sysMenuMapper.selectMenuListByUserId(userId);
        }
        return sysMenuList;
    }
}
