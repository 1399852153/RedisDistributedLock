package com.xiongyx.util;

import com.xiongyx.annotation.RedisLock;
import com.xiongyx.annotation.RedisLockKey;
import com.xiongyx.constants.RedisConstants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author xiongyx
 * @date 2019/5/21
 */
public class RedisLockKeyUtil {

    /**
     * 拼接redis最终的key
     * */
    public static String getFinallyLockKey(RedisLock annotation, ProceedingJoinPoint proceedingJoinPoint){
        StringBuilder keyStringBuilder = new StringBuilder();

        // 拼接当前方法所在类名
        String className = proceedingJoinPoint.getTarget().getClass().getSimpleName();
        keyStringBuilder.append(className)
            .append(RedisConstants.KEY_SEPARATOR);

        // 拼接当前方法所在方法名
        String methodName = proceedingJoinPoint.getSignature().getName();
        keyStringBuilder.append(methodName)
            .append(RedisConstants.KEY_SEPARATOR);

        // 拼接注解中的key
        keyStringBuilder.append(annotation.lockKey())
            .append(RedisConstants.KEY_SEPARATOR);

        // 拼接注解参数中的值
        String paramKey = getRedisLockKeyFormParam(proceedingJoinPoint);
        if(!StringUtils.isEmpty(paramKey)){
            keyStringBuilder.append(paramKey)
                .append(RedisConstants.KEY_SEPARATOR);
        }

        return keyStringBuilder.toString();
    }

    /**
     * 从参数中获取拼接key的元素
     * */
    private static String getRedisLockKeyFormParam(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();

        // 方法参数
        Object[] args = proceedingJoinPoint.getArgs();

        for(int i=0; i<parameterAnnotations.length; i++){
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            Object arg = args[i];

            for(Annotation itemAnnotation : parameterAnnotation){
                if (itemAnnotation instanceof RedisLockKey) {
                    RedisLockKey redisKeyLockAnnotation = (RedisLockKey)itemAnnotation;
                    switch (redisKeyLockAnnotation.type()) {
                        case ALL:
                            return getRedisKeyByAll(arg);
                        case FIELD:
                            return getRedisKeyByField(redisKeyLockAnnotation.expression(),arg);
                        default:
                            throw new RuntimeException(String.format("error RedisLockKey.type: %s",
                                redisKeyLockAnnotation.type()));
                    }
                }
            }
        }

        return "";
    }

    /**
     * ALL类型，当前参数直接通过toString返回
     * */
    private static String getRedisKeyByAll(Object arg){
        return arg.toString();
    }

    private static String getRedisKeyByField(String expression,Object arg) {
        String setMethodName = makeGetMethodName(expression);

        try{
            String currentArgKey;
            if(arg instanceof Map){
                // map中获取key值对应的value
                Object field = ((Map)arg).get(expression);
                currentArgKey = getFieldKey(field);

                return currentArgKey;
            }else{
                //:::获得bean中key的method对象
                Method beanGetMethod = arg.getClass().getMethod(setMethodName);
                //:::调用获得当前的key
                Object field = beanGetMethod.invoke(arg);
                currentArgKey = getFieldKey(field);

                return currentArgKey;
            }
        }catch (Exception e){
            throw new RuntimeException("getRedisKeyByField error",e);
        }
    }

    /**
     * 获得field对应的Key
     * */
    private static String getFieldKey(Object keyField){
        if(keyField == null){
            throw new RuntimeException("keyField is null");
        }

        if(isSimpleType(keyField)){
            return keyField.toString();
        }else{
            throw new RuntimeException("keyField is not a simpleType");
        }
    }

    /***
     * 将通过keyName获得对应的bean对象的get方法名称的字符串
     * @param keyName 属性名
     * @return 返回get方法名称的字符串
     */
    private static String makeGetMethodName(String keyName) {
        //:::将第一个字母转为大写
        String newKeyName = transFirstCharUpperCase(keyName);

        return "get" + newKeyName;
    }

    /**
     * 将字符串的第一个字母转为大写
     * @param str 需要被转变的字符串
     * @return 返回转变之后的字符串
     */
    private static String transFirstCharUpperCase(String str) {
        return str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toUpperCase());
    }

    /**
     * 判断是否是简单类型
     * */
    private static boolean isSimpleType(Object keyField){
        String simpleClassName = keyField.getClass().getSimpleName();

        if (simpleClassName.equals("int") || simpleClassName.equals("Integer")) {
            return true;
        }
        if (simpleClassName.equals("long") || simpleClassName.equals("Long")) {
            return true;
        }
        if (simpleClassName.equals("double") || simpleClassName.equals("Double")) {
            return true;
        }
        if (simpleClassName.equals("boolean") || simpleClassName.equals("Boolean")) {
            return true;
        }
        if (simpleClassName.equals("float") || simpleClassName.equals("Float")) {
            return true;
        }
        if (simpleClassName.equals("String")) {
            return true;
        }

        return false;
    }
}
