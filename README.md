# Konnect Search

This is a simulation of how we would want to Kong Konnect to enable search on CDC entitesent (services, routes, nodes).
Workflow
1. A service reads the CDC events present in a jsonl file.
2. Another service pushes the events to Kafka.
3. A consumer reads from Kafka and sanitizes the events and sends them to an OpenSearch indexer.
4. The indexer indexes the JSON entries to OpenSearch and makes it available for search based on different fields.

## Get started

Run 

```
docker-compose up -d
```

to start a Kakfa cluster. 

The cluster is accessible locally at `localhost:9092` or `kafka:29092` for services running inside the container network.


You can also access Kafka-UI at `localhost:8080` to examine the ingested Kafka messages.

Opensearch is accessible locally at `localhost:9200` or `opensearch-node:9200` 
for services running inside the container network.

You can validate Opensearch is working by running sample queries

Insert
```
curl -X PUT localhost:9200/cdc/_doc/1 -H "Content-Type: application/json" -d '{"foo": "bar"}'
{"_index":"cdc","_id":"1","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":0,"_primary_term":1}%
```

Search
```
curl localhost:9200/cdc/_search  | python -m json.tool
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   223  100   223    0     0  41527      0 --:--:-- --:--:-- --:--:-- 44600
{
    "took": 1,
    "timed_out": false,
    "_shards": {
        "total": 1,
        "successful": 1,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": {
            "value": 1,
            "relation": "eq"
        },
        "max_score": 1.0,
        "hits": [
            {
                "_index": "cdc",
                "_id": "1",
                "_score": 1.0,
                "_source": {
                    "foo": "bar"
                }
            }
        ]
    }
}
```

Run

```
docker-compose down
```

to tear down all the services. 

## Resources

* `stream.jsonl` contains cdc events that need to be ingested
* `docker-compose.yaml` contains the skeleton services to help you get started
