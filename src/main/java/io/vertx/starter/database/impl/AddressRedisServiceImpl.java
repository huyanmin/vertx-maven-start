package io.vertx.starter.database.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.starter.database.AddressRedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @Author Ginny Hu
 * @create 2020/4/7 14:20
 */
public class AddressRedisServiceImpl implements AddressRedisService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AddressRedisServiceImpl.class);

  private RedisClient redisClient;

  public AddressRedisServiceImpl(RedisClient redisClient, Handler<AsyncResult<AddressRedisService>> readyHandler) {
    this.redisClient = redisClient;
    readyHandler.handle(Future.succeededFuture(this));
  }

  @Override
  public AddressRedisService fetchAllAddresses(Handler<AsyncResult<Set<String>>> resultHandler) {
    redisClient.keys("*", result->{
      if (result.succeeded()) {
        Set<String> jsonObjects = new HashSet<>();
        JsonArray result1 = result.result();
        result1.forEach(key->{
          redisClient.get(key.toString(), res-> {
            if(Objects.nonNull(res.result())){
              jsonObjects.add(res.result().toString());
            }
            resultHandler.handle(Future.succeededFuture(jsonObjects));
          });
        });
      } else {
        LOGGER.error("Database query error", result.cause());
        resultHandler.handle(Future.failedFuture(result.cause()));
      }
    });
    return this;
  }

  @Override
  public AddressRedisService fetchAddress(String id, Handler<AsyncResult<Set<String>>> resultHandler) {
    Set<String> jsonObjects = new HashSet<>();
    redisClient.get(id, res-> {
      if(res.failed()){
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
      if(Objects.nonNull(res.result())){
        jsonObjects.add(res.result().toString());
      }
      resultHandler.handle(Future.succeededFuture(jsonObjects));

    });
    return this;
  }

  @Override
  public AddressRedisService refreshAddress(JsonObject jsonObject, Handler<AsyncResult<Boolean>> resultHandler) {
    redisClient.set(jsonObject.getString("id"), jsonObject.encodePrettily(), result->{
        if (result.failed()) {
          LOGGER.error("redis add cache error", result.cause());
          resultHandler.handle(Future.failedFuture(result.cause()));
        }
        resultHandler.handle(Future.succeededFuture(true));
      });
    return this;
  }

  @Override
  public AddressRedisService addAddressCache(JsonObject jsonObject, Handler<AsyncResult<Boolean>> resultHandler) {
    redisClient.set(jsonObject.getString("id"), jsonObject.encodePrettily(), result->{
      if (result.failed()) {
        LOGGER.error("redis add cache error", result.cause());
        resultHandler.handle(Future.failedFuture(result.cause()));
      }
      resultHandler.handle(Future.succeededFuture(true));
    });
    return this;
  }

  @Override
  public AddressRedisService delAddressCache(String id, Handler<AsyncResult<Boolean>> resultHandler) {
    redisClient.del(id,result->{
        if (result.failed()) {
          LOGGER.error("redis add cache error", result.cause());
          resultHandler.handle(Future.failedFuture(result.cause()));
        }
        resultHandler.handle(Future.succeededFuture(true));
      });
    return this;
  }

  @Override
  public AddressRedisService delAllAddressCache(Handler<AsyncResult<Boolean>> resultHandler) {
    redisClient.flushdb(result->{
      if (result.failed()) {
        LOGGER.error("redis add cache error", result.cause());
        resultHandler.handle(Future.failedFuture(result.cause()));
      }
      resultHandler.handle(Future.succeededFuture(true));
    });
    return this;
  }
}
