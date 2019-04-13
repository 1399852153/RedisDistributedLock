package com.xiongyx.aspect;

import com.xiongyx.annotation.RedisLock;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.xml.ws.Action;
import java.lang.reflect.Method;

/**
 * @Author xiongyx
 * @Date 2019/4/12
 */

@Component
@Aspect
public class RedisLockAspect {

    @Pointcut("@annotation(com.xiongyx.annotation.RedisLock)") // 注解声明切点
    public void annotationPointcut() {
    };

    @After("annotationPointcut()")
    public void after(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RedisLock annotation = method.getAnnotation(RedisLock.class);
        System.out.println("注解式拦截 lockKey: " + annotation.lockKey());
        System.out.println("注解式拦截 expireTime: " + annotation.expireTime());
    }
}
