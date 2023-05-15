package com.atguigu.system.exception;

import com.atguigu.common.result.Result;
import com.atguigu.common.result.ResultCodeEnum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ClassName: GlobalExceptionHandle
 * Package: com.atguigu.system.exception
 * Description:
 *
 * @Author 邓瑶
 * @Create 2023/5/6 18:59
 * @Version 1.0
 */

@ControllerAdvice
public class GlobalExceptionHandle {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        System.out.println("全局..");
        e.printStackTrace();
        return Result.fail().message("执行全局异常处理....");
    }

    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public Result error(ArithmeticException e){
        System.out.println("特定..");
        e.printStackTrace();
        return Result.fail().message("执行特定异常处理....");
    }

    @ExceptionHandler(GuiguException.class)
    @ResponseBody
    public Result error(GuiguException e){
        System.out.println("自定义..");
        e.printStackTrace();
        return Result.fail().message("执行自定义异常处理....");
    }

    @ExceptionHandler(AccessDeniedException.class)  //哪个异常出现执行这个方法
    @ResponseBody   //返回json格式数据
    public Result error(AccessDeniedException e) {
        e.printStackTrace();
        return Result.fail().code(203).message("没有访问权限");
    }
}
