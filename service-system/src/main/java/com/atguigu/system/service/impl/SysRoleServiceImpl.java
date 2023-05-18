package com.atguigu.system.service.impl;

import com.atguigu.model.system.SysRole;

import com.atguigu.model.system.SysUserRole;
import com.atguigu.model.vo.AssignMenuVo;
import com.atguigu.model.vo.AssignRoleVo;
import com.atguigu.system.mapper.SysRoleMapper;
import com.atguigu.system.mapper.SysUserRoleMapper;
import com.atguigu.system.service.SysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-05-05
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    /**
     * 获取角色分配数据
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> getRoleAssignData(Long userId) {
        //1 查询所有角色，返回list集合
        List<SysRole> roleList = baseMapper.selectList(null);

        //2 查询用户之前分配过角色数据
        // 根据userId 查询 sys_user_role 查询用户对应角色id，返回List<SysUserRole>
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,userId);
        List<SysUserRole> sysUserRoleList = sysUserRoleMapper.selectList(wrapper);

        //  List<SysUserRole> --- List<Long>
        //获取用户分配所有角色id集合 List<Long>
        //创建list集合，用于封装用户所有角色id集合
        List<Long> roleIdList = new ArrayList<>();
        //sysUserRoleList遍历
        // 普通for  增强for 迭代器 forEach(stream) ListIterator
        for(SysUserRole sysUserRole:sysUserRoleList) {
            Long roleId = sysUserRole.getRoleId();//获取每个对象角色id
            roleIdList.add(roleId);//每个角色id封装到list集合里面
        }

        //3 把查询两部分数据封装map集合，返回
        Map<String,Object> map = new HashMap<>();
        //为了前端整合方便，名称key和课件一致
        map.put("allRoles",roleList); //所有角色list
        map.put("userRoleIds",roleIdList); //用户已经分配角色id列表
        return map;
    }

    /**
     * 更新用户的角色分配
     * @param assignRoleVo
     */
    @Transactional
    @Override
    public void doAssign(AssignRoleVo assignRoleVo) {

       /* LambdaQueryWrapper<SysUserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUserRole::getUserId, assignRoleVo.getUserId());
        sysUserRoleMapper.delete(lambdaQueryWrapper);

        List<Long> roleIdList = assignRoleVo.getRoleIdList();

        for (Long rodeId :roleIdList) {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(rodeId);
            sysUserRole.setUserId(assignRoleVo.getUserId());
            sysUserRoleMapper.insert(sysUserRole);
        }
    }*/

        // 创建一个查询条件的 LambdaQueryWrapper 对象
        LambdaQueryWrapper<SysUserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件，要求 SysUserRole 对象的 userId 属性与 assignRoleVo.getUserId() 相等
        lambdaQueryWrapper.eq(SysUserRole::getUserId, assignRoleVo.getUserId());
        // 执行删除操作，根据查询条件删除与该用户关联的角色记录
        sysUserRoleMapper.delete(lambdaQueryWrapper);

        // 获取角色ID列表
        List<Long> roleIdList = assignRoleVo.getRoleIdList();

        // 遍历角色ID列表，为该用户分配新的角色记录
        for (Long roleId : roleIdList) {
            // 创建一个新的 SysUserRole 对象
            SysUserRole sysUserRole = new SysUserRole();
            // 设置角色ID到 SysUserRole 对象的 roleId 属性
            sysUserRole.setRoleId(roleId);
            // 设置用户ID到 SysUserRole 对象的 userId 属性
            sysUserRole.setUserId(assignRoleVo.getUserId());
            // 执行插入操作，将 SysUserRole 对象插入到数据库中
            sysUserRoleMapper.insert(sysUserRole);
        }
    }

}
