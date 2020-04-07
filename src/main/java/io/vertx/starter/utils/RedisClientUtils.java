package io.vertx.starter.utils;

import io.vertx.core.Vertx;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisConnection;
import io.vertx.redis.client.RedisOptions;

/**
 * @Author Ginny Hu
 * @create 2020/4/7 10:28
 */
public class RedisClientUtils {

  public static void getRedis(Vertx vertx){
    RedisOptions config = new RedisOptions()
      .addConnectionString("redis://127.0.0.1:6379/1");
    Redis.createClient(vertx, config).connect(handle->{
      if(handle.succeeded()){
        RedisConnection client = handle.result();
        RedisAPI redis = RedisAPI.api(client);
      }
    });
  }
}
