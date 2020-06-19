package com.xiongyx.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author xiongyx
 * @Create 2018/4/11.
 */
public final class PropsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropsUtil.class);

    /**
     * 读取配置文件
     * */
    public static Properties loadProps(String fileName){
        Properties props = null;
        InputStream is = null;
        try{
            // 绝对路径获得输入流
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if(is == null){
                // 没找到文件,抛出异常
                throw new FileNotFoundException(fileName + " is not found");
            }
            props = new Properties();
            props.load(is);
        }catch(IOException e){
            LOGGER.error("load propertis file fail",e);
        }finally {
            if(is != null){
                try{
                    // 关闭io流
                    is.close();
                } catch (IOException e) {
                    LOGGER.error("close input Stream fail",e);
                }
            }
        }

        return props;
    }

    /**
     * 获取字符串属性(默认为空字符串)
     * */
    public static String getString(Properties properties,String key){
        // 调用重载函数 默认值为:空字符串
        return getString(properties,key,"");
    }

    /**
     * 获取字符串属性
     * */
    public static String getString(Properties properties,String key,String defaultValue){
        // key对应的value数据是否存在
        if(properties.containsKey(key)){
            return properties.getProperty(key);
        }else{
            return defaultValue;
        }
    }

    /**
     * 获取int属性 默认值为0
     * */
    public static int getInt(Properties properties,String key){
        // 调用重载函数，默认为:0
        return getInt(properties,key,0);
    }

    /**
     * 获取int属性
     * */
    public static int getInt(Properties properties,String key,int defaultValue){
        // key对应的value数据是否存在
        if(properties.containsKey(key)){
            return CastUtil.castToInt(properties.getProperty(key));
        }else{
            return defaultValue;
        }
    }

    /**
     * 获取boolean属性，默认值为false
     */
    public static boolean getBoolean(Properties properties,String key){
        return getBoolean(properties,key,false);
    }

    /**
     * 获取boolean属性
     */
    public static boolean getBoolean(Properties properties,String key,boolean defaultValue){
        // key对应的value数据是否存在
        if(properties.containsKey(key)){
            return CastUtil.castToBoolean(properties.getProperty(key));
        }else{
            return defaultValue;
        }
    }
}
