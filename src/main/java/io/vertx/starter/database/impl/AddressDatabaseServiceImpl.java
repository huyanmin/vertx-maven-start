package io.vertx.starter.database.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.starter.database.AddressDatabaseService;
import io.vertx.starter.enumpackage.SqlQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @Author Ginny Hu
 * @create 2020/4/3 9:32
 */
public class AddressDatabaseServiceImpl implements AddressDatabaseService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AddressDatabaseServiceImpl.class);

  private HashMap<SqlQuery, String> sqlQueries;

  private JDBCClient dbClient;

  public AddressDatabaseServiceImpl(JDBCClient dbClient, HashMap<SqlQuery, String> sqlQueries,
                                    Handler<AsyncResult<AddressDatabaseService>> readyHandler) {
    this.dbClient = dbClient;
    this.sqlQueries = sqlQueries;
    readyHandler.handle(Future.succeededFuture(this));
  }

  @Override
  public AddressDatabaseService fetchAllAddresses(Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    dbClient.query(sqlQueries.get(SqlQuery.ALL_ADDRESSES), result->{
      if (result.succeeded()) {
        resultHandler.handle(Future.succeededFuture(result.result().getRows()));
      } else {
        LOGGER.error("Database query error", result.cause());
        resultHandler.handle(Future.failedFuture(result.cause()));
      }
    });
    return this;
  }

  @Override
  public AddressDatabaseService fetchAddress(String id, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    dbClient.queryWithParams(sqlQueries.get(SqlQuery.GET_ADDRESS), new JsonArray().add(id), fetch -> {
      if (fetch.succeeded()) {
        resultHandler.handle(Future.succeededFuture(fetch.result().getRows()));
      } else {
        LOGGER.error("Database query error", fetch.cause());
        resultHandler.handle(Future.failedFuture(fetch.cause()));
      }
    });
    return this;
  }

  @Override
  public AddressDatabaseService createAddress(JsonObject jsonObject, Handler<AsyncResult<Boolean>> resultHandler) {
    JsonArray data = new JsonArray().add(jsonObject.getValue("id")).add(jsonObject.getValue("name"))
      .add(jsonObject.getValue("phone")).add(jsonObject.getValue("cardNo")).add(jsonObject.getValue("addressType"))
      .add(jsonObject.getValue("isDefault")).add(jsonObject.getValue("address")).add(jsonObject.getValue("remark"));
    dbClient.updateWithParams(sqlQueries.get(SqlQuery.CREATE_ADDRESS), data, res -> {
      if (res.succeeded()) {
        resultHandler.handle(Future.succeededFuture(true));
      } else {
        LOGGER.error("Database query error", res.cause());
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }

  @Override
  public AddressDatabaseService saveAddress(JsonObject jsonObject, Handler<AsyncResult<Void>> resultHandler) {
    JsonArray data = new JsonArray().add(jsonObject.getValue("name")).add(jsonObject.getValue("phone"))
      .add(jsonObject.getValue("addressType")).add(jsonObject.getValue("isDefault"))
      .add(jsonObject.getValue("address")).add(jsonObject.getValue("remark")).add(jsonObject.getValue("id"));
    dbClient.updateWithParams(sqlQueries.get(SqlQuery.SAVE_ADDRESS), data, res -> {
      if (res.succeeded()) {
        resultHandler.handle(Future.succeededFuture());
      } else {
        LOGGER.error("Database query error", res.cause());
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }

  @Override
  public AddressDatabaseService deleteAddress(String id, Handler<AsyncResult<Void>> resultHandler) {
    JsonArray data = new JsonArray().add(id);
    dbClient.updateWithParams(sqlQueries.get(SqlQuery.DELETE_ADDRESS), data, res -> {
      if (res.succeeded()) {
        resultHandler.handle(Future.succeededFuture());
      } else {
        LOGGER.error("Database query error", res.cause());
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }

  @Override
  public AddressDatabaseService deleteAllAddress(Handler<AsyncResult<Void>> resultHandler) {
    dbClient.update(sqlQueries.get(SqlQuery.DELETE_ALL_ADDRESS), res -> {
      if (res.succeeded()) {
        resultHandler.handle(Future.succeededFuture());
      } else {
        LOGGER.error("Database query error", res.cause());
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }
}
