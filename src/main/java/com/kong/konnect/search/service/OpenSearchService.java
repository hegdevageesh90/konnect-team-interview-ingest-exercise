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

/**
 * Index given JSON to OpenSearch
 *
 * @author hegdevageesh
 */
@Service
public class OpenSearchService {
  private static final Logger logger = LoggerFactory.getLogger(OpenSearchService.class);
  private final RestHighLevelClient openSearchClient;
  private final String indexName;

  public OpenSearchService(
      RestHighLevelClient openSearchClient, AppConfigProperties appConfigProperties) {
    this.openSearchClient = openSearchClient;
    this.indexName = appConfigProperties.getOpenSearch().getIndex();
  }

  /**
   * Forms a new IndexRequest and index it to OpenSearch
   *
   * @param json input JSON to be indexed
   */
  public void indexEvent(String json) {
    try {
      openSearchClient.index(
          new IndexRequest(indexName)
              .id(UUID.randomUUID().toString())
              .source(json, XContentType.JSON),
          RequestOptions.DEFAULT);
      logger.info("Indexed JSON data into OpenSearch");
    } catch (Exception e) {
      logger.error("Error indexing JSON data to OpenSearch", e);
    }
  }
}
