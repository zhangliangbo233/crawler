package com.maiya.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis公共类
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class RedisCacheUtil {

    private static final Logger logger = LoggerFactory
            .getLogger(RedisCacheUtil.class);

    private RedisTemplate redisTemplate;

    /**
     * 缓存是否开启，默认开启,true开启，false不开启
     */
    private volatile boolean redisSwitch = true;

    /**
     * 添加redis缓存，设置超时时间
     * 此超时时间会覆盖掉前一个超时时间
     *
     * @param redisKey 缓存key
     * @param object   缓存对象
     * @param timeout  超时时间
     * @param unit     超时单位
     */
    public void set(String redisKey, Object object, long timeout, TimeUnit unit) {
        if (redisSwitch) {
            try {
                if (!ObjectUtils.isEmpty(object)) {
                    redisTemplate.opsForValue().set(redisKey, JSON.toJSONString(object), timeout, unit);
                }
            } catch (Throwable e) {
                logger.error("set error:", e);
            }
        }
    }

    /**
     * 不设置超时时间的set方法
     *
     * @param redisKey
     * @param object
     */
    public void setNoExpire(String redisKey, Object object) {
        if (redisSwitch) {
            try {
                if (!ObjectUtils.isEmpty(object)) {
                    BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
                    operations.set(JSON.toJSONString(object));
                }
            } catch (Throwable e) {
                logger.error("set error:", e);
            }
        }
    }

    public void hsetNoExpire(String redisKey, String hashKey, Object object) {
        if (redisSwitch) {
            try {
                BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(redisKey);
                boundHashOperations.put(hashKey, JSON.toJSONString(object));
            } catch (Throwable e) {
                logger.error("hset error:", e);
            }
        }
    }

    public void expire(String redisKey, int seconds) {
        if (redisSwitch) {
            try {
                BoundValueOperations opt = redisTemplate.boundValueOps(redisKey);
                opt.expire(seconds, TimeUnit.SECONDS);
            } catch (Throwable e) {
                logger.error("expire error:", e);
            }
        }
    }

    public void expireAt(String redisKey, Date date) {
        if (redisSwitch) {
            try {
                BoundValueOperations opt = redisTemplate.boundValueOps(redisKey);
                opt.expireAt(date);
            } catch (Throwable e) {
                logger.error("expireAt error:", e);
            }
        }
    }

    /**
     * 添加redis的map缓存，设置超时时间
     *
     * @param redisKey 缓存key
     * @param hashKey
     * @param object   缓存对象
     * @param date     超时时间点
     */
    public void hset(String redisKey, String hashKey, Object object, Date date) {
        if (redisSwitch) {
            try {
                if (!ObjectUtils.isEmpty(object)) {
                    BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(redisKey);
                    boundHashOperations.put(hashKey, JSON.toJSONString(object));
                    boundHashOperations.expireAt(date);
                }
            } catch (Throwable e) {
                logger.error("hset error:", e);
            }
        }
    }

    /**
     * 添加redis的map缓存，设置超时时间
     * 此超时时间会覆盖掉前一个超时时间
     *
     * @param redisKey 缓存key
     * @param hashKey
     * @param object   缓存对象
     * @param timeout  超时时间
     * @param unit     超时单位
     * @author jijun
     * @date 2014年7月15日
     */
    public void hset(String redisKey, String hashKey, Object object, long timeout, TimeUnit unit) {
        if (redisSwitch) {
            try {
                if (!ObjectUtils.isEmpty(object)) {
                    BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(redisKey);
                    boundHashOperations.put(hashKey, JSON.toJSONString(object));
                    boundHashOperations.expire(timeout, unit);
                }
            } catch (Throwable e) {
                logger.error("hset error:", e);
            }
        }
    }

    public void hsetAll(String redisKey, Map<String, String> map, long timeout, TimeUnit unit) {
        if (redisSwitch) {
            try {
                BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(redisKey);
                boundHashOperations.putAll(map);
                boundHashOperations.expire(timeout, unit);
            } catch (Throwable e) {
                logger.error("hsetAll error:", e);
            }
        }
    }

    /**
     * 获取redis非list缓存
     *
     * @param redisKey
     * @param clazz
     * @return
     */
    public <T> T get(String redisKey, Class<T> clazz) {
        if (redisSwitch) {
            try {
                String objectJson = (String) redisTemplate.opsForValue().get(redisKey);
                if (StringUtils.isBlank(objectJson)) {
                    return null;
                }
                return JSON.parseObject(objectJson, clazz);
            } catch (Throwable e) {
                logger.error("get error:", e);
            }
        }
        return null;
    }

    /**
     * Description: 获取hash缓存内容，如果key/field不存在则返回null
     *
     * @param key
     * @param field
     * @return key-field对应的value
     */
    public String get(String key, String field) {
        return hget(key, field);
    }


    public Long increment(String redisKey, String hashKey, long delta) {
        if (redisSwitch) {
            return redisTemplate.opsForHash().increment(redisKey, hashKey, delta);
        }
        return null;
    }

    /**
     * 获取redis的list缓存
     *
     * @param redisKey
     * @param clazz
     * @return
     */
    public <T> List<T> getList(String redisKey, Class<T> clazz) {
        if (redisSwitch) {
            try {
                String objectJson = (String) redisTemplate.opsForValue().get(redisKey);
                if (StringUtils.isBlank(objectJson)) {
                    return new ArrayList<T>();
                }
                return JSON.parseArray(objectJson, clazz);
            } catch (Throwable e) {
                logger.error("getList error:", e);
            }
        }

        return new ArrayList<T>();
    }

    /**
     * 获取redis的map缓存
     *
     * @param redisKey
     * @param hashKey
     * @param clazz
     * @return
     * @author jijun
     * @date 2014年7月10日
     */
    public <T> T hget(String redisKey, String hashKey, Class<T> clazz) {
        if (redisSwitch) {
            try {
                String objectJson = (String) redisTemplate.opsForHash().get(redisKey, hashKey);
                if (StringUtils.isBlank(objectJson)) {
                    return null;
                }
                return JSON.parseObject(objectJson, clazz);
            } catch (Throwable e) {
                logger.error("hget error:", e);
            }
        }
        return null;
    }

    /**
     * 从Hash中获取对象,转换成制定类型
     */
    public <T> T hget(String key, String field, Type clazz) {

        String jsonContext = get(key, field);

        return (T) JSONObject.parseObject(jsonContext, clazz);
    }

    /**
     * 从哈希表key中获取field的value
     *
     * @param key
     * @param field
     */

    public String hget(String key, String field) {
        if (redisSwitch) {
            try {
                String objectJson = (String) redisTemplate.opsForHash().get(key, field);
                if (StringUtils.isBlank(objectJson)) {
                    return null;
                }
                return objectJson;
            } catch (Throwable e) {
                logger.error("hget error:", e);
            }
        }
        return null;
    }

    /**
     * 获取redis中map的list缓存
     *
     * @param redisKey
     * @param hashKey
     * @param clazz
     * @return
     * @author jijun
     * @date 2014年7月10日
     */
    public <T> List<T> hgetList(String redisKey, String hashKey, Class<T> clazz) {
        if (redisSwitch) {
            try {
                String objectJson = (String) redisTemplate.opsForHash().get(redisKey, hashKey);
                if (StringUtils.isBlank(objectJson)) {
                    return null;
                }
                return JSON.parseArray(objectJson, clazz);
            } catch (Throwable e) {
                logger.error("hgetList error:", e);
            }
        }
        return new ArrayList<T>();
    }

    /**
     * 删除redis某个key的缓存
     *
     * @param redisKey
     * @author jijun
     * @date 2014年7月10日
     */
    public void delete(String redisKey) {
        if (redisSwitch) {
            try {
                redisTemplate.delete(redisKey);
            } catch (Throwable e) {
                logger.error("delete error:", e);
            }
        }
    }

    /**
     * 删除redis中map的key
     *
     * @param redisKey
     * @param hashKeys
     * @author jijun
     * @date 2014年7月10日
     */
    public void hdelete(String redisKey, String... hashKeys) {
        if (redisSwitch) {
            try {
                redisTemplate.opsForHash().delete(redisKey, hashKeys);
            } catch (Throwable e) {
                logger.error("hdelete error:", e);
            }
        }
    }

    public Map<String, String> hgetall(String key) {
        if (redisSwitch) {
            try {
                HashOperations<String, String, String> opt = redisTemplate.opsForHash();
                return opt.entries(key);
            } catch (Throwable e) {
                logger.error("hgetall error:", e);
            }
        }
        return null;
    }

    /**
     * 根据hashkeys列表获取缓存列表
     *
     * @param key
     * @param hashKeys
     * @return
     */
    public List<String> hgetall(String key, Collection<String> hashKeys) {
        if (redisSwitch) {
            try {
                HashOperations<String, String, String> opt = redisTemplate.opsForHash();
                return opt.multiGet(key, hashKeys);
            } catch (Throwable e) {
                logger.error("hgetall error:", e);
            }
        }
        return null;
    }

    public <T> List<T> hgetall(String key, Collection<String> hashKeys, Class<T> targetClass) {
        if (redisSwitch) {
            try {
                HashOperations<String, String, String> opt = redisTemplate.opsForHash();
                List<String> jsonList = opt.multiGet(key, hashKeys);
                if (jsonList != null && !jsonList.isEmpty()) {
                    return JSON.parseArray(jsonList.toString(), targetClass);
                }
            } catch (Throwable e) {
                logger.error("hgetall error:", e);
            }
        }
        return null;
    }


    /**
     * 重置redis的过期时间
     *
     * @param redisKey
     * @return
     */
    public boolean resetExpireTime(String redisKey, long timeout, TimeUnit unit) {
        if (redisSwitch) {
            try {
                return redisTemplate.boundValueOps(redisKey).expire(timeout, unit);
            } catch (Exception e) {
                logger.error("resetExpireTime error:", e);
                return false;
            }
        }
        return true;
    }

    public int incrAtDayTime(String key, int i) {
        if (redisSwitch) {
            Long l = redisTemplate.boundValueOps(key).increment(i);
            if (l != null) {
                return l.intValue();
            }
        }
        return -1;
    }


    public int incr(String key, int i) {
        if (redisSwitch) {
            Long l = redisTemplate.boundValueOps(key).increment(i);
            if (l != null) {
                return l.intValue();
            }
        }
        return -1;
    }

    /**
     * hash自增
     *
     * @param key
     * @param hashKey
     * @param i
     * @return
     */
    public int incr(String key, String hashKey, int i) {
        if (redisSwitch) {
            Long l = redisTemplate.boundHashOps(key).increment(hashKey, i);
            if (l != null) {
                return l.intValue();
            }
        }
        return -1;
    }

    /**
     * hash自减
     *
     * @param key
     * @param i
     * @return
     */
    public int decr(String key, String hashKey, int i) {
        if (redisSwitch) {
            Long l = redisTemplate.boundHashOps(key).increment(hashKey, -i);
            if (l != null) {
                return l.intValue();
            }
        }
        return -1;
    }

    public int decr(String key, int i) {
        if (redisSwitch) {
            Long l = redisTemplate.boundValueOps(key).increment(-i);
            if (l != null) {
                return l.intValue();
            }
        }
        return -1;
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

}
