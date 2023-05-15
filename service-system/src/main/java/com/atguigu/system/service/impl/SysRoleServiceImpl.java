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

    @Transactional
    @Override
    public void doAssign(AssignRoleVo assignRoleVo) {

        LambdaQueryWrapper<SysUserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUserRole::getUserId, assignRoleVo.getUserId());
        sysUserRoleMapper.delete(lambdaQueryWrapper);

        List<Long> roleIdList = assignRoleVo.getRoleIdList();

        for (Long rodeId :roleIdList) {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(rodeId);
            sysUserRole.setUserId(assignRoleVo.getUserId());
            sysUserRoleMapper.insert(sysUserRole);
        }
    }
}
