package com.atguigu.system.service.impl;

import com.atguigu.common.result.ResultCodeEnum;
import com.atguigu.model.system.SysMenu;
import com.atguigu.model.system.SysRoleMenu;
import com.atguigu.model.system.SysUserRole;
import com.atguigu.model.vo.AssignMenuVo;
import com.atguigu.model.vo.AssignRoleVo;
import com.atguigu.system.exception.GuiguException;
import com.atguigu.system.mapper.SysMenuMapper;
import com.atguigu.system.mapper.SysRoleMenuMapper;
import com.atguigu.system.service.SysMenuService;
import com.atguigu.system.utils.MenuHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.swing.text.Style;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-05-10
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public List<SysMenu> findNodes() {
        //查找所有节点
        List<SysMenu> sysMenus = baseMapper.selectList(null);
        if (CollectionUtils.isEmpty(sysMenus)) {
            return null;
        }
        List<SysMenu> menus = MenuHelper.buildTree(sysMenus);
        return menus;
    }

    @Override
    public void deleteMenuId(Long id) {

        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        //List<SysMenu> sysMenuList = baseMapper.selectList(wrapper);
        Integer count = baseMapper.selectCount(wrapper);

        //判断
        if (count > 0) { //有子菜单，不能删除
            //抛出异常 自定义异常
            throw new GuiguException(ResultCodeEnum.NODE_ERROR);
        }
        //没有子菜单，删除
        baseMapper.deleteById(id);
    }

    @Override
    public List<SysMenu> toAssign(Long roleId) {
        //查询所有菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1);
        List<SysMenu> sysMenuList = baseMapper.selectList(wrapper);

        //分配菜单数据
        LambdaQueryWrapper<SysRoleMenu> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuMapper.selectList(wrapper1);

        List<Long> roleMenuIds = new ArrayList<>();
        for (SysRoleMenu sysRoleMenu : sysRoleMenus) {
            Long menuId = sysRoleMenu.getMenuId();
            roleMenuIds.add(menuId);
        }

        //4 把第一步所有菜单集合遍历，得到每个菜单id
        // 拿着每个菜单id 和第三步集合比较，如果第三步集合有id，设置isSelect=true
        for (SysMenu sysMenu : sysMenuList) {
            Long id = sysMenu.getId();
            if (roleMenuIds.contains(id)) {
                sysMenu.setSelect(true);
            } else {
                sysMenu.setSelect(false);
            }
        }
        //调用工具类，返回需要数据，树形结构
        List<SysMenu> sysMenus = MenuHelper.buildTree(sysMenuList);
        return sysMenus;
    }

    @Transactional
    @Override
    public void doAssign(AssignMenuVo assignMenuVo) {

        //1 根据角色id删除角色之前分配过菜单数据 sys_role_menu
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, assignMenuVo.getRoleId());
        sysRoleMenuMapper.delete(wrapper);

        //2 添加角色和菜单关系数据
        //menuIdList新分配菜单id集合，遍历，添加
        List<Long> menuIdList = assignMenuVo.getMenuIdList();
        for (Long menuId : menuIdList) {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(assignMenuVo.getRoleId());
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenuMapper.insert(sysRoleMenu);
        }
    }

    /**
     * 根据用户id查找菜单列表
     * @param id
     * @return
     */
    @Override
    public List<String> findPermsListByUserId(Long id) {
        List<SysMenu> sysMenuList = null;
        if (id.longValue() == 1) {
            // 如果用户ID为1，即超级管理员，获取所有启用状态的菜单列表
            sysMenuList = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1));
        } else {
            // 否则，根据用户ID查询用户的菜单列表
            sysMenuList = baseMapper.selectMenuListByUserId(id);
        }

        // 过滤出菜单类型为2的菜单，并提取权限信息
        List<String> permsList = sysMenuList.stream()
                .filter(item -> item.getType() == 2)
                .map(item -> item.getPerms())
                .collect(Collectors.toList());

        return permsList;
    }
}
