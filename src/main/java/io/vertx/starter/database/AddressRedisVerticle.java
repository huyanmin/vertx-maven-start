package io.vertx.starter.database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * @Author Ginny Hu
 * @create 2020/4/7 10:58
 */
public class AddressRedisVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> promise) throws Exception {
    // 连接Redis的参数
    RedisOptions redisOptions = new RedisOptions();

    // 获取到Redis客户端
    RedisClient redisClient = RedisClient.create(vertx,redisOptions);

   AddressRedisService.create(redisClient, handle->{
      if(handle.succeeded()){
        new ServiceBinder(vertx)
          .setAddress("redis-service-address")
          .register(AddressRedisService.class, handle.result());
        promise.complete();
      }else {
        promise.fail(handle.cause());
      }
    });
  }
}
