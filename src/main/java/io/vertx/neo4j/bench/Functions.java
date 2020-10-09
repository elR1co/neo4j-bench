package io.vertx.neo4j.bench;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.vertx.neo4j.bench.model.MyNode;
import io.vertx.neo4j.bench.model.MyRelationship;
import io.vertx.reactivex.WriteStreamSubscriber;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import org.neo4j.driver.Record;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static io.vertx.neo4j.bench.Constants.COMMA_BUFFER;
import static io.vertx.neo4j.bench.Constants.HEADER_BUFFER;

public final class Functions {

    private Functions() {}

    public static WriteStreamSubscriber<Buffer> createSubscriber(HttpServerResponse response, AtomicLong numberOfRecords, long start) {
        return response.setChunked(true).toSubscriber()
                .onError(handleError("Unable to stream", response))
                .onWriteStreamError(handleError("Error while streaming data", response))
                .onWriteStreamEndError(handleError("Error while closing write stream", response))
                .onWriteStreamEnd(() -> System.out.println("Time " + TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS) + " ms, Number of records : " + numberOfRecords.get()));
    }

    public static Function<List<Record>, Buffer> fromRecordsToBuffer(AtomicLong numberOfRecords) {
        return records -> {
            io.vertx.core.buffer.Buffer buffer = io.vertx.core.buffer.Buffer.buffer();

            records.forEach(record -> {
                MyNode leftNode = new MyNode(record.get("n").asNode());
                MyNode rightNode = new MyNode(record.get("m").asNode());
                MyRelationship relationship = new MyRelationship(record.get("r").asRelationship());

                buffer.appendBuffer(numberOfRecords.getAndIncrement() == 0 ? HEADER_BUFFER : COMMA_BUFFER);
                buffer.appendBuffer(leftNode.toJson().toBuffer());
                buffer.appendBuffer(COMMA_BUFFER);
                buffer.appendBuffer(rightNode.toJson().toBuffer());
                buffer.appendBuffer(COMMA_BUFFER);
                buffer.appendBuffer(relationship.toJson().toBuffer());
            });

            return Buffer.newInstance(buffer);
        };
    }

    public static Consumer<Throwable> handleError(String errorMessage, HttpServerResponse response) {
        return error -> {
            System.err.println(errorMessage);
            error.printStackTrace();
            response.setStatusCode(500).setStatusMessage(errorMessage).end();
        };
    }
}
