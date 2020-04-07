package io.vertx.starter.database;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Redis;
import io.vertx.starter.database.impl.AddressRedisServiceImpl;

import java.util.List;
import java.util.Set;

/**
 * @Author Ginny Hu
 * @create 2020/4/7 14:17
 */
@ProxyGen
@VertxGen
public interface AddressRedisService {

  @Fluent
  AddressRedisService fetchAllAddresses(Handler<AsyncResult<Set<String>>> resultHandler);

  @Fluent
  AddressRedisService fetchAddress(String id, Handler<AsyncResult<Set<String>>> resultHandler);

  @Fluent
  AddressRedisService refreshAddress(JsonObject jsonObject, Handler<AsyncResult<Boolean>> resultHandler);

  @Fluent
  AddressRedisService addAddressCache(JsonObject jsonObject, Handler<AsyncResult<Boolean>> resultHandler);

  @Fluent
  AddressRedisService delAddressCache(String id, Handler<AsyncResult<Boolean>> resultHandler);

  // tag::create[]
  @GenIgnore
  static AddressRedisService create(Redis client, Vertx vertx, Handler<AsyncResult<AddressRedisService>> readyHandler) {
    return new AddressRedisServiceImpl(client, vertx, readyHandler);
  }

  @GenIgnore
  static AddressRedisService createProxy(Vertx vertx, String address) {
    return new AddressRedisServiceVertxEBProxy(vertx, address);
  }
}
