package com.fhf.jedis;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class JedisLearn {


    private static final String REDIS_IP = "192.168.190.100";
    private static final int REDIS_PORT = 6379;

    @Test
    public void jedisTest(){

        //初始化jedis对象
        Jedis jedis = new Jedis(REDIS_IP, REDIS_PORT);

        System.out.println(jedis.ping());
        jedis.set("testKey", "testValue");

        System.out.println(jedis.get("testKey"));
        Set<String> keys = jedis.keys("*");
        for(String key:keys){
            System.out.print(key + " ");
        }
        System.out.println();
        System.out.println(jedis.ttl("testKey"));

        System.out.println(jedis.get("teKey"));
        jedis.close();
    }

    @Test
    public  void testVerifyCode(){
        PhoneValidate phoneValidate = new PhoneValidate();
        String myPhoneNum = "13940440072";
        String verificationCode = phoneValidate.getVerificationCode(myPhoneNum);
        System.out.println(verificationCode);
        if(phoneValidate.judgeVerifyCode(myPhoneNum,  verificationCode)){
            System.out.println("verification success");
        }else{
            System.out.println("verification failed");
        }
    }
}
