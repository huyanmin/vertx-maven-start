package io.vertx.starter.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.starter.constant.Constant;
import io.vertx.starter.database.AddressDatabaseService;
import io.vertx.starter.database.AddressRedisService;
import io.vertx.starter.utils.HttpUtils;
import io.vertx.starter.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @Author Ginny Hu
 * @create 2020/4/3 14:11
 */
public class HttpServerVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

  private AddressDatabaseService dbService;

  private AddressRedisService redisService;

  @Override
  public void start(Promise<Void> promise) throws Exception {
    HttpServer server = vertx.createHttpServer();
    String addressDbQueue = config().getString(Constant.CONFIG_ADDRESS_DB_QUEUE, Constant.CONFIG_ADDRESS_DB_QUEUE); // <1>
    String addressRedisQueue = config().getString(Constant.CONFIG_REDIS_QUEUE, Constant.CONFIG_REDIS_QUEUE); // <1>

    //读取配置文件
    PropertiesUtils propertiesUtils = new PropertiesUtils(Constant.CONFIG_HTTP_FILENAME);
    Properties queriesProps = propertiesUtils.readProperties();

    //创建代理
    dbService = AddressDatabaseService.createProxy(vertx, addressDbQueue);
    redisService = AddressRedisService.createProxy(vertx, addressRedisQueue);

    Router router = Router.router(vertx);
    router.get("/").handler(this::indexHandler);
    router.get("/address/:id").handler(this::pageRenderingHandler);
    router.post().handler(BodyHandler.create());
    router.post("/address/save").handler(this::pageUpdateHandler);
    router.post("/address/create").handler(this::pageCreateHandler);
    router.post("/address/delete").handler(this::pageDeletionHandler);
    router.post("/address/delete/all").handler(this::pageDeletionAllHandler);

    Integer port = Integer.parseInt(queriesProps.getProperty(Constant.CONFIG_HTTP_SERVER_PORT));
    int portNumber = config().getInteger(Constant.CONFIG_HTTP_SERVER_PORT, port);
    server
      .requestHandler(router)
      .listen(portNumber, ar -> {
        if (ar.succeeded()) {
          LOGGER.info("HTTP server running on port " + port);
          promise.complete();
        } else {
          LOGGER.error("Could not start a HTTP server", ar.cause());
          promise.fail(ar.cause());
        }
      });
  }

  /**
   * 查询所有收货地址
   * @param routingContext
   */
  private void indexHandler(RoutingContext routingContext) {
    dbService.fetchAllAddresses(reply -> {
      if (reply.succeeded()) {
        HttpUtils.fireJsonResponse(routingContext.response(), HTTP_OK, reply.result().toString());
      } else {
        routingContext.fail(reply.cause());
      }
    });
  }

  /**
   * 根据id查询收获地址
   * @param routingContext
   */
  private void pageRenderingHandler(RoutingContext routingContext) {
    String id = routingContext.request().getParam("id");
    redisService.fetchAddress(id, response->{
      if(response.failed()){
        routingContext.response().end(response.cause().getMessage());
      }
      //判断缓存是否存在，不存在则去查数据库
      if(Objects.nonNull(response.result()) && response.result().size() > 0){
        HttpUtils.fireJsonResponse(routingContext.response(), HTTP_OK, response.result().toString());
      }else {
        dbService.fetchAddress(id, result->{
          if(result.succeeded()){
              if(Objects.nonNull(result.result()) && result.result().size() > 0){
                redisService.addAddressCache(result.result().get(0), handler-> handler.succeeded());
                HttpUtils.fireJsonResponse(routingContext.response(), HTTP_OK, result.result().toString());
              }else {
                HttpUtils.fireJsonResponse(routingContext.response(), HTTP_OK, "not find address data!");
              }
          }else {
            routingContext.fail(result.cause());
          }
        });
      }
    });
  }

  /**
   * 修改地址
   * @param routingContext
   */
  private void pageUpdateHandler(RoutingContext routingContext) {
    JsonObject jsonObject = new JsonObject(routingContext.getBody());
    dbService.saveAddress(jsonObject, reply->{
      if(reply.succeeded()){
        redisService.refreshAddress(jsonObject, res->res.succeeded());
        HttpUtils.fireJsonResponse(routingContext.response(), HTTP_OK, "update address successfully!");
      }else {
        routingContext.fail(reply.cause());
      }
    });
  }

  /**
   * 新增地址
   * @param routingContext
   */
  private void pageCreateHandler(RoutingContext routingContext) {
    JsonObject jsonObject = new JsonObject(routingContext.getBody());
    String id = UUID.randomUUID().toString().replace("-","");
    jsonObject.put("id", id);
    redisService.addAddressCache(jsonObject, handler-> handler.succeeded());
    dbService.createAddress(jsonObject, reply->{
      if(reply.succeeded()){
        HttpUtils.fireJsonResponse(routingContext.response(), HTTP_OK, "create address successfully!");
      }else {
        routingContext.fail(reply.cause());
      }
    });
  }

  /**
   * 删除地址
   * @param routingContext
   */
  private void pageDeletionHandler(RoutingContext routingContext) {
    JsonObject jsonObject = new JsonObject(routingContext.getBody());
    String id = jsonObject.getValue("id").toString();
    dbService.deleteAddress(id, reply->{
      if(reply.succeeded()){
        redisService.delAddressCache(id, res-> res.succeeded());
        HttpUtils.fireJsonResponse(routingContext.response(), HTTP_OK, "delete address successfully!");
      }else {
        routingContext.fail(reply.cause());
      }
    });
  }

  /**
   * 删除所有地址
   * @param routingContext
   */
  private void pageDeletionAllHandler(RoutingContext routingContext) {
    dbService.deleteAllAddress(reply->{
      if(reply.succeeded()){
        redisService.delAllAddressCache(handler-> handler.succeeded());
        HttpUtils.fireJsonResponse(routingContext.response(), HTTP_OK, "delete all address successfully!");
      }else {
        routingContext.fail(reply.cause());
      }
    });
  }
}
