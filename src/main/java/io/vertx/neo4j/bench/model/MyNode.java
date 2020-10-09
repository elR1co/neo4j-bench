package io.vertx.neo4j.bench.model;

import io.vertx.core.json.JsonObject;
import org.neo4j.driver.types.Node;

public class MyNode {

    private final Long id;

    public MyNode(Node node) {
        this.id = node.id();
    }

    public JsonObject toJson() {
        return new JsonObject().put("id", id);
    }
}
