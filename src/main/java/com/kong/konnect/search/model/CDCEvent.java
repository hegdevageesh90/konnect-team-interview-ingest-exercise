package com.kong.konnect.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CDCEvent(
        @JsonProperty("before") Before before,
        @JsonProperty("after") After after,
        @JsonProperty("op") String op,
        @JsonProperty("ts_ms") long tsMs
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Before() {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record After(
            @JsonProperty("key") String key,
            @JsonProperty("value") Value value
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Value(
                @JsonProperty("type") int type,
                @JsonProperty("object") ObjectNode object
        ) {}
    }

    public Map<String, Object> toMap() {
        return Map.of(
                "before", before,
                "after", after,
                "op", op,
                "ts_ms", tsMs
        );
    }
}
