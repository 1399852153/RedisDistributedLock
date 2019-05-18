package com.xiongyx.service.api;

import java.util.List;

/**
 * @author xiongyx
 * on 2019/5/18.
 */
public interface RedisClient {

    String get(final String key);

    void set(final String key, final String value);

    Object eval(String script, List<String> keys, List<String> args);
}
