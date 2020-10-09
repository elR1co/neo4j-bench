package io.vertx.neo4j.bench;

import io.vertx.reactivex.core.buffer.Buffer;

public final class Constants {

    private Constants() {}

    public static final String MATCH_ALL_NODES_AND_RELATIONSHIPS = "MATCH (n)-[r]->(m) RETURN n, r, m LIMIT 500000";

    public static final io.vertx.core.buffer.Buffer HEADER_BUFFER = io.vertx.core.buffer.Buffer.buffer()
            .appendString("{")
            .appendString("\"messageType\"")
            .appendString(":")
            .appendString(String.valueOf(1))
            .appendString(",")
            .appendString("\"payload\"")
            .appendString(":")
            .appendString("[")
            ;

    public static final io.vertx.core.buffer.Buffer COMMA_BUFFER = io.vertx.core.buffer.Buffer.buffer().appendString(",");

    public static final Buffer FOOTER_BUFFER = Buffer.newInstance(io.vertx.core.buffer.Buffer.buffer()
            .appendString("]")
            .appendString("}")
    );
}
