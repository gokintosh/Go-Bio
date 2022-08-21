package com.gobio.gobio;


import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.ext.web.client.WebClient;
import io.vertx.rxjava3.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerVerticle extends AbstractVerticle {

    private WebClient webClient;



    @Override
    public Completable rxStart(){

        BodyHandler bodyHandler = BodyHandler.create();

        Router router = Router.router(vertx);
        router.post().handler(bodyHandler);
        router.put().handler(bodyHandler);

        String prefix="/api/v1";

        router.get("/api/hello").handler(this::sayHello);

        router.post(prefix+"/register").handler(this::register);
        router.post(prefix+"/token").handler(this::token);


        router.get(prefix+"/:username/:year/:month")
                        .handler(jwthandler)
                                .handler(this::checkUser)
                                        .handler(this::monthlySteps);

        webClient =WebClient.create(vertx);

        return vertx.createHttpServer()
                .requestHandler(router)
                .rxListen(8080).ignoreElement();

    }

    public void sayHello(RoutingContext ctx) {
        ctx.response().end("Hello World!!");
    }

    private void register(RoutingContext ctx){
        webClient.post(3000,"localhost","/register")
                .putHeader("Content-Type","application/json")
                .rxSendJson(ctx.getBodyAsJson())
                .subscribe(
                        bufferHttpResponse -> sendStatausCode(ctx,bufferHttpResponse.statusCode()),
                        err->sendBadGateway(ctx,err)
                );
    }

    private void sendStatausCode(RoutingContext ctx,int code){
        ctx.response().setStatusCode(code);
    }

    private void sendBadGateway(RoutingContext ctx,Throwable err){
        log.error("Woops!!",err);
        ctx.fail(502);
    }
}
