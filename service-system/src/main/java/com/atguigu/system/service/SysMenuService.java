package com.atguigu.system.service;

import com.atguigu.model.system.SysMenu;
import com.atguigu.model.vo.AssignMenuVo;
import com.atguigu.model.vo.AssignRoleVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-05-10
 */
public interface SysMenuService extends IService<SysMenu> {

    //菜单列表
    List<SysMenu> findNodes();

    //删除菜单
    void deleteMenuId(Long id);

    List<SysMenu> toAssign(Long roleId);

    void doAssign(AssignMenuVo assignMenuVo);

    List<String> findPermsListByUserId(Long id);
}
