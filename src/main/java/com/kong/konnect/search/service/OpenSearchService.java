package com.kong.konnect.search.service;

import com.kong.konnect.search.config.properties.OpenSearchProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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
  private RestHighLevelClient openSearchClient;
  private final String indexName;

  public OpenSearchService(
      RestHighLevelClient openSearchClient, OpenSearchProperties openSearchProperties) {
    this.openSearchClient = openSearchClient;
    this.indexName = openSearchProperties.getIndex();
  }

  /**
   * Forms a new IndexRequest and index it to OpenSearch
   *
   * @param json input JSON to be indexed
   */
  @CircuitBreaker(name = "openSearchCircuitBreaker", fallbackMethod = "fallback")
  @Retry(name = "openSearchRetry")
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

  /**
   * Fallback method for circuit breaker.
   *
   * @param json the JSON that failed to index
   * @param ex the exception that triggered the fallback
   */
  public void fallback(String json, Throwable ex) {
    logger.error("OpenSearch Circuit breaker activated. Fallback triggered for JSON: {}", json, ex);
    // TODO : add fallback impl. example : index to a backup queue/cluster.
  }

  public void setOpenSearchClient(RestHighLevelClient openSearchClient) {
    this.openSearchClient = openSearchClient;
  }
}
