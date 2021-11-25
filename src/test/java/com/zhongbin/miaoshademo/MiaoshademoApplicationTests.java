package com.zhongbin.miaoshademo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
class MiaoshademoApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisScript<Boolean> redisScript;

    @Test
    public void contextLoads(){
        ValueOperations valueOperations = redisTemplate.opsForValue();

        Boolean isLock = valueOperations.setIfAbsent("k1", "v1");

        if(isLock){
            valueOperations.set("name", "xxxx");
            String name = (String)valueOperations.get("name");
            System.out.println("name:" + name);

            //模拟出现异常
            //策略思路：1. 设置redis过期时间(不行)
            //        2. LUA脚本。redis原生支持LUA脚本，把几个命令（获取锁，判断锁，删除锁）原子化
            Integer.parseInt("xxx");

            redisTemplate.delete("k1");
        }else {
            System.out.println("有线程在使用请稍后");
        }
    }

    @Test
    public void testLock03(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String uuid = UUID.randomUUID().toString();
        //设置失效时间防止在运行中跑出了异常导致锁无法正常释放
        Boolean isLock = valueOperations.setIfAbsent("k1", uuid, 5, TimeUnit.SECONDS);
        if(isLock){
            valueOperations.set("name", "xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println(name);
            String k1 = (String) valueOperations.get("k1");
            Boolean result = (Boolean) redisTemplate.execute(redisScript, Collections.singletonList("k1"), uuid);
            System.out.println(result);
        }else {
            System.out.println("有线程在使用请稍后");
        }
    }
}
