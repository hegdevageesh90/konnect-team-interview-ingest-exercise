package com.kong.konnect.search.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "com.kong.konnect.search")
public class AppConfigProperties {

  private final OpenSearchProperties openSearch;
  private final KafkaProperties kafka;
  private final CdcProperties cdc;

  public AppConfigProperties(
      OpenSearchProperties openSearch, KafkaProperties kafka, CdcProperties cdc) {
    this.openSearch = openSearch;
    this.kafka = kafka;
    this.cdc = cdc;
  }

  public OpenSearchProperties getOpenSearch() {
    return openSearch;
  }

  public KafkaProperties getKafka() {
    return kafka;
  }

  public CdcProperties getCdc() {
    return cdc;
  }

  public record OpenSearchProperties(String host, int port, String scheme, String index) {
    public String getHost() {
      return host;
    }

    public int getPort() {
      return port;
    }

    public String getScheme() {
      return scheme;
    }

    public String getIndex() {
      return index;
    }
  }

  public record KafkaProperties(
      String topicName, short replicationFactor, int partitions, String consumerGroupId) {
    public String getTopicName() {
      return topicName;
    }

    public short getReplicationFactor() {
      return replicationFactor;
    }

    public int getPartitions() {
      return partitions;
    }

    public String getConsumerGroupId() {
      return consumerGroupId;
    }
  }

  public record CdcProperties(String filePath) {
    public String getFilePath() {
      return filePath;
    }
  }
}
