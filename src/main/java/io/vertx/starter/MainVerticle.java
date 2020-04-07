package io.vertx.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.starter.database.AddressDatabaseVerticle;
import io.vertx.starter.database.AddressRedisVerticle;
import io.vertx.starter.utils.Runner;

public class MainVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Runner.runExample(MainVerticle.class);
  }

  @Override
  public void start(Promise<Void> promise) throws Exception {
    Promise<String> dbVerticleDeployment = Promise.promise();
    Promise<String> redisVerticleDeployment = Promise.promise();
    vertx.deployVerticle(new AddressDatabaseVerticle(), dbVerticleDeployment);
    vertx.deployVerticle(new AddressRedisVerticle(), redisVerticleDeployment);

    dbVerticleDeployment.future().compose(id -> {

      Promise<String> httpVerticleDeployment = Promise.promise();
      vertx.deployVerticle(
        "io.vertx.starter.http.HttpServerVerticle",
        new DeploymentOptions().setInstances(2),
        httpVerticleDeployment);

      return httpVerticleDeployment.future();

    }).setHandler(ar -> {
      if (ar.succeeded()) {
        promise.complete();
      } else {
        promise.fail(ar.cause());
      }
    });
  }
}
