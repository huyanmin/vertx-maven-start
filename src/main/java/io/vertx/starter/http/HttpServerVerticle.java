package io.vertx.starter.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
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

  private void indexHandler(RoutingContext routingContext) {
    dbService.fetchAllAddresses(reply -> {
      if (reply.succeeded()) {
        routingContext.response().putHeader("content-type", "application/json").end(reply.result().toString());

      } else {
        routingContext.fail(reply.cause());
      }
    });
  }

  private void pageRenderingHandler(RoutingContext routingContext) {

  }

  private void pageUpdateHandler(RoutingContext routingContext) {

  }

  private void pageCreateHandler(RoutingContext routingContext) {

  }

  private void pageDeletionHandler(RoutingContext routingContext) {

  }

}
