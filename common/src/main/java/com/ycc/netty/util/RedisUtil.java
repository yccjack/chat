package com.ycc.netty.util;

import com.ycc.netty.constant.ConfigConstant;
import io.netty.util.internal.StringUtil;
import redis.clients.jedis.Jedis;

/**
 * @author ycc
 * @version 1.0
 * @date 2019/1/22 15:36
 */
public class RedisUtil {
    private static RedisUtil redisUtil;
    private static Jedis jedis;

    private RedisUtil() {
    }


    public static RedisUtil getInstance() {
        if (redisUtil == null) {
            synchronized (RedisUtil.class) {
                redisUtil = new RedisUtil();
            }
        }
        return redisUtil;
    }

    static {
        try {
            jedis = new Jedis(ConfigConstant.SERVER_HOST, 6379);
        } catch (Exception e) {
           e.printStackTrace();
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
