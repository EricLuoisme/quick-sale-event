package com.selfStudy.quicksaleevent.redis;

import com.alibaba.fastjson.JSON;
import com.selfStudy.quicksaleevent.config.RedisConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisService {

    JedisPool jedisPool; // injected by constructor

    RedisConfig redisConfig; // injected by constructor

    public RedisService(JedisPool jedisPool, RedisConfig redisConfig) {
        this.jedisPool = jedisPool;
        this.redisConfig = redisConfig;
    }

    public JedisPool JedisPoolFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait() * 1000); // let it become second instead of million second
        JedisPool jedisPool = new JedisPool(poolConfig, redisConfig.getHost(),
                redisConfig.getPort(), redisConfig.getTimeout() * 1000, redisConfig.getPassword(), 0);
        return jedisPool;
    }

    public <T> boolean set(String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource(); // get a connection resource from connection pool
            String strVal = beanToString(value);
            if (strVal == null || strVal.length() <= 0)
                return false;
            jedis.set(key, strVal);
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    private <T> String beanToString(T value) {
        if (value == null)
            return null;
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class
                || clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else {
            return JSON.toJSONString(value); // when T is an object
        }
    }


    public <T> T get(String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = jedis.get(key);
            T t = stringToBean(str, clazz);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }

    private <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null)
            return null;
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz); // when T is a object class
        }
    }

    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close(); // return to the connection pool
        }
    }


}
