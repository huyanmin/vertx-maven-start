package io.vertx.starter.database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisOptions;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.starter.utils.Runner;

/**
 * @Author Ginny Hu
 * @create 2020/4/7 10:58
 */
public class AddressRedisVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> promise) throws Exception {
    RedisOptions config = new RedisOptions()
      .addConnectionString("redis://127.0.0.1:6379/1");
    Redis client = Redis.createClient(vertx, config);
    AddressRedisService.create(client, vertx, handle->{
      if(handle.succeeded()){
        new ServiceBinder(vertx)
          .setAddress("redis-service-address")
          .register(AddressRedisService.class, handle.result());
        promise.complete();
      }else {
        promise.fail(handle.cause());
      }
    });
//    JsonObject jsonObject = new JsonObject();
//    jsonObject.put("id", "1784");
//    jsonObject.put("name", "张先生");
//    jsonObject.put("phone", "18165342632");
////    addressRedisService.addAddressCache(jsonObject, res->{
////      System.out.println(res.result());
////    });
//    addressRedisService.fetchAddress("1784", res->{
//      System.out.println(res.result());
//    });
////    addressRedisService.fetchAllAddresses(handle->{
////      System.out.println(handle.result());
////    });
////    addressRedisService.refreshAddress(jsonObject, res->{
////      System.out.println(res.result());
////    });
////     addressRedisService.delAddressCache("mykey", res->{
////      System.out.println(res.result());
////    });
  }
}
