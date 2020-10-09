package io.vertx.neo4j.bench;


import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.neo4j.bench.handler.ReactiveNeo4jClientHandler;
import io.vertx.neo4j.bench.handler.VertxNeo4jClientHandler;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;

import static io.vertx.core.http.HttpMethod.GET;

public class HttpServer extends AbstractVerticle {

    private VertxNeo4jClientHandler vertxNeo4jClientHandler;
    private ReactiveNeo4jClientHandler reactiveNeo4jClientHandler;

    @Override
    public Completable rxStart() {
        this.vertxNeo4jClientHandler = new VertxNeo4jClientHandler(vertx);
        this.reactiveNeo4jClientHandler = new ReactiveNeo4jClientHandler();

        return Single.fromCallable(() -> {
            Router router = Router.router(vertx);
            router.route("/path1").method(GET).handler(vertxNeo4jClientHandler);
            router.route("/path2").method(GET).handler(reactiveNeo4jClientHandler);
            return router;
        }).flatMapCompletable(router -> {
            HttpServerOptions httpServerOptions = new HttpServerOptions()
                    .setUseAlpn(true)
                    .setCompressionLevel(6)
                    .setCompressionSupported(true);

            return vertx.createHttpServer(httpServerOptions)
                    .requestHandler(router)
                    .rxListen(8080)
                    .ignoreElement();
        });

    }

    @Override
    public Completable rxStop() {
        return Completable.mergeArray(
                vertxNeo4jClientHandler.close(),
                reactiveNeo4jClientHandler.close()
        );
    }
}
