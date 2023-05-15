package com.atguigu.system.controller;


import com.atguigu.common.result.Result;
import com.atguigu.model.system.SysRole;
import com.atguigu.model.vo.AssignRoleVo;
import com.atguigu.model.vo.SysRoleQueryVo;
import com.atguigu.system.service.SysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 角色 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-05-05
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation(value = "根据用户获取角色数据")
    @GetMapping("/toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId) {
        Map<String,Object> resultMap = sysRoleService.getRoleAssignData(userId);
        return Result.ok(resultMap);
    }

    //分配角色
    @ApiOperation(value = "根据用户分配角色")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssignRoleVo assignRoleVo) {
//        当前分配用户id
//        private Long userId;
//
//        用户分配角色id列表
//        private List<Long> roleIdList;
        sysRoleService.doAssign(assignRoleVo);
        return Result.ok();
    }

/*    用security查询后的结果：
    {"code":200,"message":"成功","data":
        [{"id":1,"createTime":"2021-05-31 18:09:18",
            "updateTime":"2022-06-08 09:21:10",
            "isDeleted":0,"param":{},"roleName":"系统管理员","roleCode":"SYSTEM","description":"系统管理员"},
        {"id":2,"createTime":"2021-06-01 08:38:40",
                "updateTime":"2022-02-24 10:42:46","isDeleted":0,"param":{},"roleName":"普通管理员","roleCode":"COMMON","description":"普通管理员"},
        {"id":3,"createTime":"2022-06-08 17:39:04",
                "updateTime":"2023-05-09 17:45:50","isDeleted":0,"param":{},"roleName":"用户管理员","roleCode":"yhgly","description":null},
        {"id":4,"createTime":"2023-05-08 17:47:22",
                "updateTime":"2023-05-09 17:45:54","isDeleted":0,"param":{},"roleName":"测试管理员","roleCode":"deng","description":null}]}*/
    //查询所有
    @ApiOperation("查询所有角色")
    @GetMapping("findAll")
    public Result findAll() {
        //模拟异常效果
//        try {
//            int p = 5/0;
//        }catch (Exception e) {
//            //手动抛出自定义异常
//            throw new GuiguException(2023,"执行自定义异常处理");
//        }

        List<SysRole> list = sysRoleService.list();
        //return Result.ok();
        //Result.fail();
        return Result.ok(list);
    }

    //根据id获取角色
    @ApiOperation(value = "根据id获取角色")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable Long id) {
        SysRole roleServiceById = sysRoleService.getById(id);
        return Result.ok(roleServiceById);
    }

    //新增角色
    @PreAuthorize("hasAuthority('bnt.sysRole.add')")
    @ApiOperation(value = "新增角色")
    @PostMapping("/save")
    public Result save(@RequestBody SysRole sysRole) {
        boolean save = sysRoleService.save(sysRole);
        return Result.ok(save);
    }

    //修改角色
    @ApiOperation(value = "修改角色")
    @PutMapping("/update")
    public Result update(@RequestBody @Validated SysRole sysRole) {
        boolean updateById = sysRoleService.updateById(sysRole);
        return Result.ok(updateById);
    }

    //删除角色
    @ApiOperation(value = "删除角色")
    @DeleteMapping("/remove/{id}")
    public Result update(@PathVariable Long id) {
        boolean removeById = sysRoleService.removeById(id);
        return Result.ok(removeById);
    }

    //根据id列表删除
    @ApiOperation(value = "根据id列表删除")
    @DeleteMapping("/batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean removeByIds = sysRoleService.removeByIds(idList);
        return Result.ok(removeByIds);
    }

    //条件分页查询
    // 条件值
    // 分页 当前页current   每页显示记录数limit
    @ApiOperation("条件分页查询")
    @GetMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable Long current,
                                @PathVariable Long limit,
                                SysRoleQueryVo sysRoleQueryVo) {
        //1 创建page对象，封装分页参数
        //参数：当前页 和 每页记录数
        Page<SysRole> pageParam = new Page<>(current, limit);

        //2 封装条件
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();

        //条件非空判断 null  ""
        if (!StringUtils.isEmpty(roleName)) {
            wrapper.like(SysRole::getRoleName, roleName);
        }

        //3 调用service方法实现条件分页查询
        Page<SysRole> pageModel = sysRoleService.page(pageParam, wrapper);
        return Result.ok(pageModel);
    }

}

