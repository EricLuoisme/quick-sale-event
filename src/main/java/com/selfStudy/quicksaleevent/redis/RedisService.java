package com.selfStudy.quicksaleevent.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisService {

    JedisPool jedisPool; // injected by constructor

    public RedisService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }


    // For setting object and push into Redis
    public <T> boolean set(KeyPrefix prefix, String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource(); // get a connection resource from connection pool
            String strVal = beanToString(value);
            if (strVal == null || strVal.length() <= 0)
                return false;
            // create a real key that combine the belonging info
            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.expireSeconds();
            if (seconds <= 0)
                jedis.set(realKey, strVal); // never expire
            else
                jedis.setex(realKey, seconds, strVal); // with the expire second parameter setting
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    // For retrieving object from Redis
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            // create a real key that combine the belonging info
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            String str = jedis.get(realKey);
            T t = stringToBean(str, clazz);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }

    // For checking key's existence
    public <T> boolean exist(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            // create a real key that combine the belonging info
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.exists(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    // For increasing this input key's value by 1. No key would return -1. Automatic
    public <T> Long incr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            // create a real key that combine the belonging info
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    // For decreasing this input key's value by 1. No key would return -1. Automatic
    public <T> Long decr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            // create a real key that combine the belonging info
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    // below functions are using to convert java object to json object, vise versa

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
        if (jedis != null)
            jedis.close(); // return to the connection pool
    }

}
