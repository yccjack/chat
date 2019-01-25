package com.ycc.netty.util;

import io.netty.util.internal.StringUtil;
import redis.clients.jedis.Jedis;

/**
 * @author ycc
 * @version 1.0
 * @date 2019/1/22 15:36
 */
public class RedisUtil {
    private static Jedis jedis;

    private RedisUtil() {
    }

    static {
        try {
            jedis = new Jedis("192.168.6.211", 6379);
            System.out.println("Redis Util Is Already");
        } catch (Exception e) {
            System.out.println("请检查Jedis配置.");
        }
    }

    public static String get(String key) {
        if (StringUtil.isNullOrEmpty(key)) {
            return null;
        } else {
            return jedis.get(key);
        }
    }

    public static long del(String key) {
        if (StringUtil.isNullOrEmpty(key)) {
            return -1;
        } else {
            jedis.del(key);
            return 1;
        }
    }

    public static String set(String key, String value) {
        return jedis.set(key, value);
    }

}
