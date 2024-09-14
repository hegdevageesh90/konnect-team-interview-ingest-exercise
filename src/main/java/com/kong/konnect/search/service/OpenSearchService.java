package com.kong.konnect.search.service;

import com.kong.konnect.search.config.AppConfigProperties;
import java.util.UUID;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OpenSearchService {
  private static final Logger logger = LoggerFactory.getLogger(OpenSearchService.class);
  private final RestHighLevelClient openSearchClient;
  private final AppConfigProperties appConfigProperties;

  public OpenSearchService(
      RestHighLevelClient openSearchClient, AppConfigProperties appConfigProperties) {
    this.openSearchClient = openSearchClient;
    this.appConfigProperties = appConfigProperties;
  }

  public void indexEvent(String json) {
    try {
      IndexRequest indexRequest =
          new IndexRequest(appConfigProperties.getOpenSearch().getIndex())
              .id(UUID.randomUUID().toString())
              .source(json, XContentType.JSON);
      openSearchClient.index(indexRequest, RequestOptions.DEFAULT);
      logger.info("Indexed JSON data into OpenSearch");
    } catch (Exception e) {
      logger.error("Error indexing JSON data to OpenSearch", e);
    }
  }
}
