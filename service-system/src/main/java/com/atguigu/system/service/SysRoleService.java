package com.atguigu.system.service;

import com.atguigu.model.system.SysRole;
import com.atguigu.model.vo.AssignMenuVo;
import com.atguigu.model.vo.AssignRoleVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 角色 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-05-05
 */
public interface SysRoleService extends IService<SysRole> {

    Map<String,Object> getRoleAssignData(Long userId);

    void doAssign(AssignRoleVo assignRoleVo);
}
