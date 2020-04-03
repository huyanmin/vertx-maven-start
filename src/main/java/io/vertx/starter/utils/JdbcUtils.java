package io.vertx.starter.utils;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * @Author Ginny Hu
 * @create 2020/4/3 10:36
 */
public class JdbcUtils {

  public static JDBCClient getDbClient(Vertx vertx){
    JsonObject dbConfig = new JsonObject();
    dbConfig.put("url", "jdbc:mysql://localhost:3306/address");
    dbConfig.put("driver_class", "com.mysql.jdbc.Driver");
    dbConfig.put("user", "root");
    dbConfig.put("password", "123456");
    return JDBCClient.createShared(vertx, dbConfig);
  }
}
