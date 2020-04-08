package io.vertx.starter.database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.starter.constant.Constant;

/**
 * @Author Ginny Hu
 * @create 2020/4/7 10:58
 */
public class AddressRedisVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> promise) throws Exception {

    RedisOptions redisOptions = new RedisOptions();

    RedisClient redisClient = RedisClient.create(vertx,redisOptions);

   AddressRedisService.create(redisClient, handle->{
      if(handle.succeeded()){
        new ServiceBinder(vertx)
          .setAddress(Constant.CONFIG_REDIS_QUEUE)
          .register(AddressRedisService.class, handle.result());
        promise.complete();
      }else {
        promise.fail(handle.cause());
      }
    });
  }
}
