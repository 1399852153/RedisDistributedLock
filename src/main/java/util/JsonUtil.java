package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @Author xiongyx
 * on 2019/4/9.
 *
 * json工具类
 */
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 对象转json字符串
     * */
    public static <T> String objToJsonStr(T obj){
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json字符串转对象
     * */
    public static <T> T jsonStrToObj(String jsonStr, Class<T> clazz){
        try {
            return OBJECT_MAPPER.readValue(jsonStr,clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
