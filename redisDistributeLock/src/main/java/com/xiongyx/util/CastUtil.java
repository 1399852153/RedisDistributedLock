package com.xiongyx.util;

/**
 * @Author xiongyx
 * @Create 2018/4/13.
 *
 * 类型转换工具类
 */
public final class CastUtil {

    /**
     * 转为 string
     * */
    public static String castToString(Object obj){
        return castToString(obj,"");
    }

    /**
     * 转为 string 提供默认值
     * */
    public static String castToString(Object obj,String defaultValue){
        if(obj == null){
            return defaultValue;
        }else{
            return obj.toString();
        }
    }

    /**
     * 转为 int
     * */
    public static int castToInt(Object obj){
        return castToInt(obj,0);
    }

    /**
     * 转为 int 提供默认值
     * */
    public static int castToInt(Object obj,int defaultValue){
        if(obj == null){
            return defaultValue;
        }else{
            return Integer.parseInt(obj.toString());
        }
    }

    /**
     * 转为 double
     * */
    public static double castToDouble(Object obj){
        return castToDouble(obj,0);
    }

    /**
     * 转为 double 提供默认值
     * */
    public static double castToDouble(Object obj,double defaultValue){
        if(obj == null){
            return defaultValue;
        }else{
            return Double.parseDouble(obj.toString());
        }
    }

    /**
     * 转为 long
     * */
    public static long castToLong(Object obj){
        return castToLong(obj,0);
    }

    /**
     * 转为 long 提供默认值
     * */
    public static long castToLong(Object obj,long defaultValue){
        if(obj == null){
            return defaultValue;
        }else{
            return Long.parseLong(obj.toString());
        }
    }

    /**
     * 转为 boolean
     * */
    public static boolean castToBoolean(Object obj){
        return  castToBoolean(obj,false);
    }

    /**
     * 转为 boolean 提供默认值
     * */
    public static boolean castToBoolean(Object obj,boolean defaultValue){
        if(obj == null){
            return defaultValue;
        }else{
            return Boolean.parseBoolean(obj.toString());
        }
    }
}
