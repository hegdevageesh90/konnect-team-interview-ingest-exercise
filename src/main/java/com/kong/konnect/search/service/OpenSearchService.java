package com.kong.konnect.search.service;

import com.kong.konnect.search.model.CDCEvent;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.action.index.IndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OpenSearchService {
    private static final Logger logger = LoggerFactory.getLogger(OpenSearchService.class);
    private final RestHighLevelClient openSearchClient;

    public OpenSearchService(RestHighLevelClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }

    public void indexEvent(CDCEvent event) {
        try {
            IndexRequest indexRequest = new IndexRequest("konnect-entities").source(event.toMap());
            openSearchClient.index(indexRequest, RequestOptions.DEFAULT);
            logger.info("Indexed event into OpenSearch");
        } catch (Exception e) {
            logger.error("Error indexing event to OpenSearch", e);
        }
    }
}
