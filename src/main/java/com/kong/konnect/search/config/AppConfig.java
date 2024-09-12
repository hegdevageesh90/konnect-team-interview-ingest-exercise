package com.kong.konnect.search.config;

import org.apache.http.HttpHost;
import org.apache.kafka.clients.admin.NewTopic;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public NewTopic konnectTopic() {
        return new NewTopic("konnect-cdc-events", 1, (short) 1);
    }

    @Bean
    public RestHighLevelClient openSearchClient() {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );
    }
}
