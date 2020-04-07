package io.vertx.starter.http;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.starter.database.AddressDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author Ginny Hu
 * @create 2020/4/3 14:11
 */
public class HttpServerVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

  public static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
  public static final String CONFIG_WIKIDB_QUEUE = "database-service-address";

  private AddressDatabaseService dbService;


  @Override
  public void start(Promise<Void> promise) throws Exception {
    HttpServer server = vertx.createHttpServer();
    String wikiDbQueue = config().getString(CONFIG_WIKIDB_QUEUE, "database-service-address"); // <1>

    //创建代理
    dbService = AddressDatabaseService.createProxy(vertx, wikiDbQueue);

    Router router = Router.router(vertx);
    router.get("/").handler(this::indexHandler);
    router.get("/address/:id").handler(this::pageRenderingHandler);
    router.post().handler(BodyHandler.create());
    router.post("/address/save").handler(this::pageUpdateHandler);
    router.post("/address/create").handler(this::pageCreateHandler);
    router.post("/address/delete").handler(this::pageDeletionHandler);

    int portNumber = config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080);
    server
      .requestHandler(router)
      .listen(portNumber, ar -> {
        if (ar.succeeded()) {
          LOGGER.info("HTTP server running on port " + 8080);
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
        routingContext.response().putHeader("content-type", "application/json").end(reply.result().toString());
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
    dbService.fetchAddress(id, response->{
      if(response.succeeded()){
        routingContext.response().putHeader("content-type", "application/json").end(response.result().toString());
      }else {
        routingContext.fail(response.cause());
      }
    });
  }

  /**
   * 修改地址
   * @param routingContext
   */
  private void pageUpdateHandler(RoutingContext routingContext) {
    Buffer body = routingContext.getBody();
    JsonObject jsonObject = new JsonObject(body.toString());
    dbService.saveAddress(jsonObject, reply->{
      if(reply.succeeded()){
        routingContext.response().end("update address successfully!");
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
    Buffer body = routingContext.getBody();
    JsonObject jsonObject = new JsonObject(body.toString());
    dbService.createAddress(jsonObject, reply->{
      if(reply.succeeded()){
        routingContext.response().end("create address successfully!");
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
    Buffer body = routingContext.getBody();
    JsonObject jsonObject = new JsonObject(body.toString());
    String id = jsonObject.getValue("id").toString();
    dbService.deleteAddress(id, reply->{
      if(reply.succeeded()){
        routingContext.response().end("delete address successfully!");
      }else {
        routingContext.fail(reply.cause());
      }
    });
  }

}
