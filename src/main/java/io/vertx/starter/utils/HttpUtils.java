package io.vertx.starter.utils;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;


/**
 * Created by XHD on 2018/3/15.
 */
public class HttpUtils {

    public static void fireJsonResponse(HttpServerResponse response, int statusCode, String payload) {
        response.putHeader("content-type", "application/json; charset=utf-8").setStatusCode(statusCode).end(payload);
    }
}
