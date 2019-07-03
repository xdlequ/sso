package com.sso.Dao.Impl;

import com.sso.Dao.JedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


/**
 * Created by ql on 2019/7/3.
 */

@Component
public class JedisClientImpl implements JedisClient {

    @Autowired
    JedisPool jedisPool;

    @Override
    public String get(String key) {
        Jedis jedis=null;
        try {
            jedis= jedisPool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            return null;
        }finally {
            jedis.close();
        }
    }

    @Override
    public String set(String key, String value) {
        Jedis jedis=null;
        try {
            jedis= jedisPool.getResource();
            return jedis.set(key,value);
        } catch (Exception e) {
            return null;
        }finally {
            jedis.close();
        }
    }

    @Override
    public String hget(String hkey, String key) {
        Jedis jedis=null;
        try {
            jedis= jedisPool.getResource();
            return jedis.hget(hkey,key);
        } catch (Exception e) {
            return null;
        }finally {
            jedis.close();
        }
    }

    @Override
    public long hset(String hkey, String key, String value) {
        Jedis jedis=null;
        try {
            jedis= jedisPool.getResource();
            return jedis.hset(hkey,key,value);
        } catch (Exception e) {
            return -1;
        }finally {
            jedis.close();
        }
    }

    @Override
    public long del(String key) {
        Jedis jedis=null;
        try {
            jedis= jedisPool.getResource();
            return jedis.del(key);
        } catch (Exception e) {
            return -1;
        }finally {
            jedis.close();
        }
    }

    @Override
    public long hdel(String hkey, String key) {
        Jedis jedis=null;
        try {
            jedis= jedisPool.getResource();
            return jedis.hdel(hkey,key);
        } catch (Exception e) {
            return -1;
        }finally {
            jedis.close();
        }
    }

    @Override
    public long expire(String key, int second) {
        Jedis jedis=null;
        try {
            jedis= jedisPool.getResource();
            return jedis.expire(key,second);
        } catch (Exception e) {
            return -1;
        }finally {
            jedis.close();
        }
    }
}
