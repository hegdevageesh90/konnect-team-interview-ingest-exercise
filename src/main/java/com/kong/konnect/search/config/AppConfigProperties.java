package com.kong.konnect.search.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author hegdevageesh
 */
@Configuration
@ConfigurationProperties(prefix = "com.kong.konnect.search")
public class AppConfigProperties {
  private OpenSearchProperties openSearch;
  private KafkaProperties kafka;
  private CdcProperties cdc;

  public static class OpenSearchProperties {
    private String host;
    private int port;
    private String scheme;
    private String index;

    public String getHost() {
      return host;
    }

    public void setHost(String host) {
      this.host = host;
    }

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }

    public String getScheme() {
      return scheme;
    }

    public void setScheme(String scheme) {
      this.scheme = scheme;
    }

    public String getIndex() {
      return index;
    }

    public void setIndex(String index) {
      this.index = index;
    }
  }

  public static class KafkaProperties {
    private String topicName;
    private short replicationFactor;
    private int partitions;
    private String consumerGroupId;

    public String getTopicName() {
      return topicName;
    }

    public void setTopicName(String topicName) {
      this.topicName = topicName;
    }

    public short getReplicationFactor() {
      return replicationFactor;
    }

    public void setReplicationFactor(short replicationFactor) {
      this.replicationFactor = replicationFactor;
    }

    public int getPartitions() {
      return partitions;
    }

    public void setPartitions(int partitions) {
      this.partitions = partitions;
    }

    public String getConsumerGroupId() {
      return consumerGroupId;
    }

    public void setConsumerGroupId(String consumerGroupId) {
      this.consumerGroupId = consumerGroupId;
    }
  }

  public static class CdcProperties {
    private String filePath;

    public String getFilePath() {
      return filePath;
    }

    public void setFilePath(String filePath) {
      this.filePath = filePath;
    }
  }

  public OpenSearchProperties getOpenSearch() {
    return openSearch;
  }

  public void setOpenSearch(OpenSearchProperties openSearch) {
    this.openSearch = openSearch;
  }

  public KafkaProperties getKafka() {
    return kafka;
  }

  public void setKafka(KafkaProperties kafka) {
    this.kafka = kafka;
  }

  public CdcProperties getCdc() {
    return cdc;
  }

  public void setCdc(CdcProperties cdc) {
    this.cdc = cdc;
  }
}
