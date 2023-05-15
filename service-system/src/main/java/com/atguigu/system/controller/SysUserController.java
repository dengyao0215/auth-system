package com.atguigu.system.controller;


import com.atguigu.common.result.Result;
import com.atguigu.common.util.MD5;
import com.atguigu.model.system.SysRole;
import com.atguigu.model.system.SysUser;
import com.atguigu.model.vo.SysRoleQueryVo;
import com.atguigu.model.vo.SysUserQueryVo;
import com.atguigu.system.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-05-09
 */
@Api(tags = "用户管理方式")
@RestController
@RequestMapping("/admin/system/sysUser")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    //更新用户状态 正常 和 禁用
    @ApiOperation(value = "更新状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id,
                               @PathVariable Integer status){
        sysUserService.updateUserStatus(id,status);
        return Result.ok();
    }

    @ApiOperation("条件查询所有用户")
    @GetMapping("/{page}/{limit}")
    public Result findQueryPage(@PathVariable Long page,
                                @PathVariable Long limit,
                                SysUserQueryVo sysUserQueryVo) {
        //1 创建page对象，封装分页参数
        //参数：当前页 和 每页记录数
        Page<SysUser> pageParam = new Page<>(page, limit);

        //2 封装条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysUserQueryVo.getKeyword();

        //条件非空判断 null  ""
        if (!StringUtils.isEmpty(roleName)) {
            wrapper.like(SysUser::getUsername, roleName)
                    .or().like(SysUser::getPhone, roleName)
                    .or().like(SysUser::getName, roleName);
        }

        //3 调用service方法实现条件分页查询
        Page<SysUser> page1 = sysUserService.page(pageParam, wrapper);
        return Result.ok(page1);
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable long id){
        sysUserService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("增加信息")
    @PostMapping("/save")
    public Result save(@RequestBody SysUser sysUser){
        //获取输入密码
        String passwordInput = sysUser.getPassword();
        //对输入密码进行MD5加密
        String passwordMD5 = MD5.encrypt(passwordInput);
        //把加密之后密码设置回到对象里面
        sysUser.setPassword(passwordMD5);

        boolean save = sysUserService.save(sysUser);
        return Result.ok();
    }

    @ApiOperation("根据id查询信息")
    @GetMapping("/get/{id}")
    public Result get (@PathVariable long id){
        SysUser byId = sysUserService.getById(id);
        return  Result.ok(byId);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Result update(@RequestBody SysUser sysUser){
        sysUserService.updateById(sysUser);
        return Result.ok();
    }
}

