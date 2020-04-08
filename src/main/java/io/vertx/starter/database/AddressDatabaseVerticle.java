package io.vertx.starter.database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.starter.enumpackage.SqlQuery;
import io.vertx.starter.utils.JdbcUtils;
import io.vertx.starter.utils.PropertiesUtils;
import io.vertx.starter.utils.Runner;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * @Author Ginny Hu
 * @create 2020/4/3 9:32
 */
public class AddressDatabaseVerticle extends AbstractVerticle {

  public static final HashMap<SqlQuery, String> sqlQueries = new HashMap<>();

//  public static void main(String[] args) {
//    Runner.runExample(AddressDatabaseVerticle.class);
//  }

  @Override
  public void start(Promise<Void> promise) throws Exception {
    HashMap<SqlQuery, String> sqlQueryStringHashMap = loadSqlQueries();

    JDBCClient dbClient = JdbcUtils.getDbClient(vertx);
    AddressDatabaseService.create(dbClient, sqlQueryStringHashMap, handle->{
      if(handle.succeeded()){
        //Register the handler
        new ServiceBinder(vertx)
          .setAddress("database-service-address")
          .register(AddressDatabaseService.class, handle.result());
        promise.complete();
      }else {
        promise.fail(handle.cause());
      }
    });

//    addressDatabaseService.fetchAddress("1", result->{
//      System.out.println(result);
//    });
  }

  /**
   * 初始化查询的接口枚举
   * @return
   * @throws IOException
   */
  private HashMap<SqlQuery, String> loadSqlQueries() throws IOException {
    PropertiesUtils propertiesUtils = new PropertiesUtils("/config/db-queries.properties");
    Properties queriesProps = propertiesUtils.readProperties();
    SqlQuery[] values = SqlQuery.values();
    for (int i = 0; i < values.length; i++) {
      sqlQueries.put(values[i], queriesProps.getProperty(values[i].name().toLowerCase().replace("_","-")));
    }
    return sqlQueries;
  }

//  /**
//   * 读取配置文件
//   * @param path
//   * @return
//   */
//  private Properties readProperties(String path){
//    InputStream queriesInputStream = getClass().getResourceAsStream(path);
//    Properties queriesProps = new Properties();
//    try {
//      queriesProps.load(queriesInputStream);
//      return queriesProps;
//    } catch (IOException e) {
//      e.printStackTrace();
//    }finally {
//      try {
//        queriesInputStream.close();
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//    }
//    return queriesProps;
//  }
}
