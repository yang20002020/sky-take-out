package com.sky.aspect;
import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 *  自定义切面，实现公共字段自动填充处理逻辑
 */
@Aspect
@Component
@Slf4j
public class AuToFillAspect {
    /**
     *  切入点
     */

    //  *.* 所有的类和所有的方法  .. 匹配任意参数     拦截添加了特定注解AutoFill的方法：  @annotation(com.sky.annotation.AutoFill)
    //同时满足两个条件 &&
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){

    }

    /**
     *  前置通知，在通知中进行公共字段的赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
       log.info("开始进行公共字段自动填充.....");

       /****/
        //获取到当前被拦截的方法上的数据库操作类型
        //举例： signature: "void com.sky.mapper.EmployeeMapper.update(Employee)"
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象

        /*
            @AutoFill(value= OperationType.UPDATE)
             void update(Employee employee);
         */
        //autoFill: "@com.sky.annotation.AutoFill(value=UPDATE)"
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
         // operationType : UPDATE
        OperationType operationType = autoFill.value();//获得数据库操作类型

        //获取到当前被拦截的方法的参数--实体对象
        /*
             @AutoFill(value= OperationType.UPDATE)
              void update(Employee employee);
             joinPoint.getArgs() 作用： 获取带参方法void update(Employee employee)的参数employee
         */
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }
       //entity: Employee
        Object entity = args[0];

        //准备赋值的数据  now : "2023-10-21T21:14:21.200"
        LocalDateTime now = LocalDateTime.now();
        // currentId : 1
        Long currentId = BaseContext.getCurrentId();

        //根据当前不同的操作类型，为对应的属性通过反射来赋值
        if(operationType == OperationType.INSERT){
            //为4个公共字段赋值
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(operationType == OperationType.UPDATE){
            //为2个公共字段赋值
            try {
                /*
                     public Method getDeclaredMethod(String name, Class<?>... parameterTypes)
                     第一个参数:方法名;  第二个参数 方法的参数
                 */

                // setUpdateTime: public void com.sky.entity.Employee.setUpdateTime(java.time.LocalDateTime)
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                //  setUpdateUser: public void com.sky.entity.Employee.setUpdateUser(java.lang.Long)
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
