package io.vertx.neo4j.bench.model;

import io.vertx.core.json.JsonObject;
import org.neo4j.driver.types.Relationship;

public class MyRelationship {

    private final long left;
    private final long right;
    private final String type;

    public MyRelationship(Relationship relationship) {
        this.left = relationship.startNodeId();
        this.right = relationship.endNodeId();
        this.type = relationship.type();
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("left", left)
                .put("right", right)
                .put("type", type);
    }
}
