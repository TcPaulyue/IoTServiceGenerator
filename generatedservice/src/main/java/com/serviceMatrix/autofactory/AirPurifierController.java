package com.serviceMatrix.autofactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.lang.Override;
import java.lang.String;

public class AirPurifierController extends AbstractVerticle {
  public AirPurifier airpurifier;

  public AirPurifierController(AirPurifier airpurifier) {
    this.airpurifier = airpurifier;
  }

  @Override
  public void start(Future fut) {
    Router router = Router.router(vertx);
    router.get("api/speed").handler(this::getspeed);
    router.post("api/speed").handler(this::setspeed);
    router.get("").handler(this::getname);
    router.post("").handler(this::setname);
    vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port",8080),result->{   if (result.succeeded()) {
                                        fut.complete();
                                    } else {
                                        fut.fail(result.cause());
                                    }});
  }

  public void getspeed(RoutingContext routingContext) {
    routingContext.response().putHeader("content-type","application/json; charset=utf-8").end(airpurifier.speed);
  }

  public void setspeed(RoutingContext routingContext) {
    String s = routingContext.getBodyAsString();
    this.airpurifier.speed = s;
    routingContext.response().setStatusCode(201).putHeader("content-type","application/json; charset=utf-8").end(s);
  }

  public void getname(RoutingContext routingContext) {
    routingContext.response().putHeader("content-type","application/json; charset=utf-8").end(airpurifier.name);
  }

  public void setname(RoutingContext routingContext) {
    String s = routingContext.getBodyAsString();
    this.airpurifier.name = s;
    routingContext.response().setStatusCode(201).putHeader("content-type","application/json; charset=utf-8").end(s);
  }
}
