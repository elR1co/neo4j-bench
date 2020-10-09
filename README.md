# Comparison vertx neo4j client / official neo4j reactive client

This project compares 2 way to request neo4j :
* with Vertx Neo4j client 
* with official Neo4j reactive client

We launch an HttpServer which has 2 requests handlers doing the same job :
1. retrieving data from Neo4j
2. transform data to json
3. stream the json into Http Response

**/path1** triggers the request handler which uses Vertx Neo4j client

**/path2** triggers the request handler which uses official Neo4j Reactive driver

By default, the request sent to Neo4j is a Cypher request which loads all first 500 000 nodes and relationships.

## How to launch

1. Install Neo4j 4.0 https://neo4j.com/docs/operations-manual/current/installation/

2. Fill Neo4j with a dataset
    
    Examples of datasets : 
    - https://github.com/johnymontana/neo4j-datasets
    - https://neo4j.com/developer/example-data/#stack-overflow

3. Start application with IntelliJ :

MainClass : io.vertx.core.Launcher 
 
VM options : -Dpasswd="your neo4j password"

Program arguments : run io.vertx.neo4j.bench.MainVerticle

4. Call http://localhost:8080/path1 or http://localhost:8080/path2



