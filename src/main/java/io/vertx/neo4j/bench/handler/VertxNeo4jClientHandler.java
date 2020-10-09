package io.vertx.neo4j.bench.handler;

import io.reactiverse.neo4j.options.Neo4jClientAuthOptions;
import io.reactiverse.neo4j.options.Neo4jClientOptions;
import io.reactiverse.reactivex.neo4j.Neo4jClient;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.vertx.core.Handler;
import io.vertx.reactivex.WriteStreamSubscriber;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.util.concurrent.atomic.AtomicLong;

import static io.vertx.neo4j.bench.Constants.FOOTER_BUFFER;
import static io.vertx.neo4j.bench.Constants.MATCH_ALL_NODES_AND_RELATIONSHIPS;
import static io.vertx.neo4j.bench.Functions.createSubscriber;
import static io.vertx.neo4j.bench.Functions.fromRecordsToBuffer;

public class VertxNeo4jClientHandler implements Handler<RoutingContext> {

    private final Neo4jClient neo4jClient;

    public VertxNeo4jClientHandler(Vertx vertx) {
        Neo4jClientAuthOptions authOptions = new Neo4jClientAuthOptions().setUsername("neo4j").setPassword(System.getProperty("passwd", "neo4j"));
        Neo4jClientOptions options = new Neo4jClientOptions().setHost("localhost").setPort(7687).setAuthOptions(authOptions);
        this.neo4jClient = Neo4jClient.createShared(vertx, options);
    }

    @Override
    public void handle(RoutingContext rc) {
        AtomicLong numberOfRecords = new AtomicLong();
        WriteStreamSubscriber<Buffer> subscriber = createSubscriber(rc.response(), numberOfRecords, System.nanoTime());

        Flowable.concat(neo4jClient.rxQueryStream(MATCH_ALL_NODES_AND_RELATIONSHIPS)
                .flatMapPublisher(stream -> stream.toFlowable().buffer(20))
                .map(fromRecordsToBuffer(numberOfRecords)), Flowable.just(FOOTER_BUFFER))
                .subscribe(subscriber);
    }

    public Completable close() {
        return Completable.fromAction(neo4jClient::close);
    }
}
