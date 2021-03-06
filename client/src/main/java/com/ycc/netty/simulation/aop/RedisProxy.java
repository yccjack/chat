package com.ycc.netty.simulation.aop;

import com.ycc.netty.util.RedisUtil;
import io.netty.util.internal.StringUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/**
 * @author :MysticalYcc
 * @date :11:12 2019/2/20
 */
public enum RedisProxy {
    /**
     * 存放信息工具实例
     */
    INSTANCE;

    public static RedisProxy getInstance() {
        return INSTANCE;
    }

    /**
     * 开启redis
     */
    public static boolean redisSwitch = false;

    private static ConcurrentMap<String, String> redisMap = new ConcurrentHashMap<>(1024*1024);

    public static String get(String key) {
        String result;
        if (StringUtil.isNullOrEmpty(key)) {
            return null;
        } else {
            if (redisSwitch) {
                result = RedisUtil.get(key);
            } else {
                result = redisMap.get(key);
            }
            return result;
        }
    }

    public static long del(String key) {
        if (StringUtil.isNullOrEmpty(key)) {
            return -1;
        } else {
            if (redisSwitch) {
                RedisUtil.del(key);
            } else {
                redisMap.remove(key);
            }
            return 1;
        }
    }

    public static void set(String key, String value) {
        if (redisSwitch) {
            RedisUtil.set(key, value);
        } else {
            redisMap.put(key, value);
        }
    }

}
