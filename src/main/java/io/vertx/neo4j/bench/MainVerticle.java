package io.vertx.neo4j.bench;

import io.reactivex.Completable;
import io.vertx.reactivex.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        return vertx.rxDeployVerticle(HttpServer.class.getName())
                .ignoreElement();
    }
}
