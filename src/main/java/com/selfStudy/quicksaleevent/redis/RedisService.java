package com.selfStudy.quicksaleevent.redis;

import com.alibaba.fastjson.JSON;
import com.selfStudy.quicksaleevent.utils.BeanStringConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisService {

    JedisPool jedisPool; // injected by constructor

    private Logger log = LoggerFactory.getLogger(RedisService.class); // for showing the key we set into Redis

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
            String strVal = BeanStringConvert.beanToString(value);
            if (strVal == null || strVal.length() <= 0)
                return false;
            // create a real key that combine the belonging info
            String realKey = prefix.getPrefix() + key;
            log.info("set key : " + realKey); // for showing the key we set into Redis
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
            log.info("neet to get key : " + realKey);
            log.info("real we get key : " + str);
            T t = BeanStringConvert.stringToBean(str, clazz);
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

    public boolean delete(KeyPrefix prefix) {
        if(prefix == null) {
            return false;
        }
        List<String> keys = scanKeys(prefix.getPrefix());
        if(keys==null || keys.size() <= 0) {
            return true;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(keys.toArray(new String[0]));
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> scanKeys(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            List<String> keys = new ArrayList<String>();
            String cursor = "0";
            ScanParams sp = new ScanParams();
            sp.match("*"+key+"*");
            sp.count(100);
            do{
                ScanResult<String> ret = jedis.scan(cursor, sp);
                List<String> result = ret.getResult();
                if(result!=null && result.size() > 0){
                    keys.addAll(result);
                }
                //再处理cursor
                cursor = ret.getCursor();
            }while(!cursor.equals("0"));
            return keys;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
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

    private void returnToPool(Jedis jedis) {
        /**
         * For let this Jedis connection return to connection pool
         */
        if (jedis != null)
            jedis.close(); // return to the connection pool
    }

}
