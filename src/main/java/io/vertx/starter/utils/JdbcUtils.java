package io.vertx.starter.utils;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.Properties;

/**
 * @Author Ginny Hu
 * @create 2020/4/3 10:36
 */
public class JdbcUtils {

  public static JDBCClient getDbClient(Vertx vertx){

    JsonObject dbConfig = new JsonObject();
    PropertiesUtils propertiesUtils = new PropertiesUtils("/config/jdbc.properties");
    Properties queriesProps = propertiesUtils.readProperties();
    dbConfig.put("url", queriesProps.getProperty("jdbcUrl"));
    dbConfig.put("driver_class", queriesProps.getProperty("driverClassName"));
    dbConfig.put("user", queriesProps.getProperty("username"));
    dbConfig.put("password", queriesProps.getProperty("password"));
    return JDBCClient.createShared(vertx, dbConfig);
  }
}
