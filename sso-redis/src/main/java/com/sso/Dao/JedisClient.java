package com.sso.Dao;

/**
 * Created by ql on 2019/7/3.
 */
public interface JedisClient {
    String get(String key);
    String set(String key,String value);

    //hset 以哈希表形式存储内容

    String hget(String hkey,String key);
    long hset(String hkey,String key,String value);
    long del(String key);

    //如果给定键存在，则移除该键

    long hdel(String hkey,String key);
    long expire(String key,int second);
}
