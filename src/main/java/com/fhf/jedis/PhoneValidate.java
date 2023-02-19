package com.fhf.jedis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Random;

public class PhoneValidate {

    private static final String REDIS_IP = "192.168.190.100";
    private static final int REDIS_PORT = 6379;
    private static Jedis jedisClient = null;

    public PhoneValidate(){
        jedisClient = new Jedis(REDIS_IP, REDIS_PORT);
    }

    public String getVerificationCode(String phoneNum){
        String visitNum = jedisClient.get("verify_send_times:"+phoneNum);
        if(visitNum != null && Integer.parseInt(visitNum) == 3){
            return "今日验证码次数已经超过三次";
        }
        if(jedisClient.exists("verify_send_flag:"+phoneNum)){
            return "请求过于频繁，请等待60秒后再请求验证码";
        }
        //随机生成验证码，并写入到缓冲中
        int verifyCode = new Random().nextInt(100000);

        //首先写入到缓冲中
        jedisClient.set("verify_code:" + phoneNum, String.valueOf(verifyCode));

        //设置带超时时间的flag
        jedisClient.set("verify_send_flag:"+phoneNum, String.valueOf(1), new SetParams().ex(60));
        //设置两分钟有效的验证码
        jedisClient.set("verify_code:"+phoneNum, String.valueOf(verifyCode), new SetParams().ex(120));
        if(visitNum == null){
            jedisClient.set("verify_send_times:"+phoneNum, "1");
        }else{
            jedisClient.incr("verify_send_times:"+phoneNum);
        }
        return String.valueOf(verifyCode);
    }

    public boolean judgeVerifyCode(String phoneNum, String verifyCode){
        String aimCode = jedisClient.get("verify_code:"+phoneNum);
        if(!verifyCode.equals(aimCode)){
            return false;
        }
        return true;
    }
}
