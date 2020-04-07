package io.vertx.starter.database.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.*;
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

  long DEFAULT_EXPIRE = 3600;

  private final Vertx vertx;
  //默认值
  private String host = "localhost";
  private int port = 6379;

  private Redis client;


  public AddressRedisServiceImpl(Redis client, Vertx vertx, Handler<AsyncResult<AddressRedisService>> readyHandler) {
    this.vertx = vertx;
    this.client = client;
    readyHandler.handle(Future.succeededFuture(this));
  }

  @Override
  public AddressRedisService fetchAllAddresses(Handler<AsyncResult<Set<String>>> resultHandler) {
    client.connect(handle->{
      RedisConnection connection = handle.result();
      RedisAPI redisAPI = RedisAPI.api(connection);
      redisAPI.keys("*", result->{
        if (result.succeeded()) {
          Set<String> jsonObjects = new HashSet<>();
          for (Response key : result.result()) {
            redisAPI.get(key.toString(), res-> {
              if(Objects.nonNull(res.result())){
                jsonObjects.add(res.result().toString());
              }
              resultHandler.handle(Future.succeededFuture(jsonObjects));
            });
          }
        } else {
          LOGGER.error("Database query error", result.cause());
          resultHandler.handle(Future.failedFuture(result.cause()));
        }
      });
    });
    return this;
  }

  @Override
  public AddressRedisService fetchAddress(String id, Handler<AsyncResult<Set<String>>> resultHandler) {
    client.connect(handle-> {
      RedisConnection connection = handle.result();
      RedisAPI redisAPI = RedisAPI.api(connection);
      Set<String> jsonObjects = new HashSet<>();
      redisAPI.get(id, res-> {
        if(res.failed()){
          resultHandler.handle(Future.failedFuture(res.cause()));
        }
        if(Objects.nonNull(res.result())){
          jsonObjects.add(res.result().toString());
        }
        resultHandler.handle(Future.succeededFuture(jsonObjects));

      });
    });
    return this;
  }

  @Override
  public AddressRedisService refreshAddress(JsonObject jsonObject, Handler<AsyncResult<Boolean>> resultHandler) {
    client.connect(handle-> {
      RedisConnection connection = handle.result();
      RedisAPI redisAPI = RedisAPI.api(connection);
      redisAPI.setex(jsonObject.getString("id"), "3600",jsonObject.encodePrettily(), result->{
        if (result.succeeded()) {
          resultHandler.handle(Future.succeededFuture(true));
        } else {
          LOGGER.error("redis add cache error", result.cause());
          resultHandler.handle(Future.failedFuture(result.cause()));
        }
      });
    });

    return this;
  }

  @Override
  public AddressRedisService addAddressCache(JsonObject jsonObject, Handler<AsyncResult<Boolean>> resultHandler) {
    client.connect(handle-> {
      RedisConnection connection = handle.result();
      RedisAPI redisAPI = RedisAPI.api(connection);
      redisAPI.setnx(jsonObject.getString("id"), jsonObject.encodePrettily(), result->{
        if (result.succeeded()) {
          resultHandler.handle(Future.succeededFuture());
        } else {
          LOGGER.error("redis add cache error", result.cause());
          resultHandler.handle(Future.failedFuture(result.cause()));
        }
      });
    });

    return this;
  }

  @Override
  public AddressRedisService delAddressCache(String id, Handler<AsyncResult<Boolean>> resultHandler) {
    client.connect(handle-> {
      RedisConnection connection = handle.result();
      RedisAPI redisAPI = RedisAPI.api(connection);
      List<String> list = new ArrayList<>();
      list.add(id);
      redisAPI.del(list,res->{
        if(res.succeeded()){
          resultHandler.handle(Future.succeededFuture(true));
        }else {
          resultHandler.handle(Future.failedFuture(res.cause()));
        }
      });
    });

    return null;
  }
}
