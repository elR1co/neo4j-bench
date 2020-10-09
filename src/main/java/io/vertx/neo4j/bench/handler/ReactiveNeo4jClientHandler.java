package io.vertx.neo4j.bench.handler;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.vertx.core.Handler;
import io.vertx.reactivex.WriteStreamSubscriber;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.reactive.RxSession;

import java.util.concurrent.atomic.AtomicLong;

import static io.vertx.neo4j.bench.Constants.FOOTER_BUFFER;
import static io.vertx.neo4j.bench.Constants.MATCH_ALL_NODES_AND_RELATIONSHIPS;
import static io.vertx.neo4j.bench.Functions.createSubscriber;
import static io.vertx.neo4j.bench.Functions.fromRecordsToBuffer;

public class ReactiveNeo4jClientHandler implements Handler<RoutingContext> {

    private final Driver driver;

    public ReactiveNeo4jClientHandler() {
        this.driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", System.getProperty("passwd", "neo4j")));
    }

    @Override
    public void handle(RoutingContext rc) {
        AtomicLong numberOfRecords = new AtomicLong();

        Scheduler scheduler = RxHelper.scheduler(rc.vertx().getOrCreateContext());
        RxSession rxSession = driver.rxSession();

        WriteStreamSubscriber<Buffer> subscriber = createSubscriber(rc.response(), numberOfRecords, System.nanoTime());

        Flowable.concat(
                Flowable.fromPublisher(rxSession.readTransaction(tx -> tx.run(MATCH_ALL_NODES_AND_RELATIONSHIPS).records())).onErrorResumeNext(error -> {
                    return Flowable.<Record>fromPublisher(rxSession.close()).concatWith(Flowable.error(error));
                })
                .buffer(20)
                .observeOn(scheduler)
                .map(fromRecordsToBuffer(numberOfRecords)), Flowable.just(FOOTER_BUFFER))
                .subscribe(subscriber);
    }

    public Completable close() {
        return Completable.fromAction(driver::close);
    }
}
