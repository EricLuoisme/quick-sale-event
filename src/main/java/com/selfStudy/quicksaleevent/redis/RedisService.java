package com.selfStudy.quicksaleevent.redis;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {

    JedisPool jedisPool; // injected by constructor

//    private Logger log = LoggerFactory.getLogger(RedisService.class); // for showing the key we set into Redis

    public RedisService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public <T> boolean set(KeyPrefix prefix, String key, T value) {
        /**
         * For setting object and push into Redis
         */
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource(); // get a connection resource from connection pool
            String strVal = beanToString(value);
            if (strVal == null || strVal.length() <= 0)
                return false;
            // create a real key that combine the belonging info
            String realKey = prefix.getPrefix() + key;
//            log.info(realKey); // for showing the key we set into Redis
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

    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        /**
         * For retrieving object from Redis
         */
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

    public <T> boolean exist(KeyPrefix prefix, String key) {
        /**
         * For checking key's existence
         */
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

    public boolean delete(KeyPrefix prefix, String key) {
        /**
         * For delete related caches in Redis
         */
        Jedis jedis = null;
        try {
            // create a real key that combine the belonging info
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            long res = jedis.del(realKey);
            return res > 0;
        } finally {
            returnToPool(jedis);
        }
    }

    public <T> Long incr(KeyPrefix prefix, String key) {
        /**
         * For increasing this input key's value by 1. No key would return -1. Automatic
         */
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

    public <T> Long decr(KeyPrefix prefix, String key) {
        /**
         * For decreasing this input key's value by 1. No key would return -1. Automatic
         */
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

    private <T> String beanToString(T value) {
        /**
         * convert Object into String for storing into Redis
         */
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
        /**
         * convert string we get from Redis to Object
         */
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
        /**
         * For let this Jedis connection return to connection pool
         */
        if (jedis != null)
            jedis.close(); // return to the connection pool
    }

}
