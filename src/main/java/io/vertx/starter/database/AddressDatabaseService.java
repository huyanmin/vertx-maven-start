package io.vertx.starter.database;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.starter.database.impl.AddressDatabaseServiceImpl;
import io.vertx.starter.entity.Address;
import io.vertx.starter.enumpackage.SqlQuery;

import java.util.HashMap;
import java.util.List;

/**
 * @Author Ginny Hu
 * @create 2020/4/3 9:31
 */
@ProxyGen
@VertxGen
public interface AddressDatabaseService {

  @Fluent
  AddressDatabaseService fetchAllAddresses(Handler<AsyncResult<List<JsonObject>>> resultHandler);

  @Fluent
  AddressDatabaseService fetchAddress(String id, Handler<AsyncResult<JsonObject>> resultHandler);

  @Fluent
  AddressDatabaseService createAddress(Address address, Handler<AsyncResult<Void>> resultHandler);

  @Fluent
  AddressDatabaseService saveAddress(Address address, Handler<AsyncResult<Void>> resultHandler);

  @Fluent
  AddressDatabaseService deleteAddress(int id, Handler<AsyncResult<Void>> resultHandler);

  // tag::create[]
  @GenIgnore
  static AddressDatabaseService create(JDBCClient dbClient, HashMap<SqlQuery, String> sqlQueries, Handler<AsyncResult<AddressDatabaseService>> readyHandler) {
    return new AddressDatabaseServiceImpl(dbClient, sqlQueries, readyHandler);
  }
  // end::create[]

  // tag::proxy[]
  @GenIgnore
  static AddressDatabaseService createProxy(Vertx vertx, String address) {
    return null;
  }
  // end::proxy[]
}
