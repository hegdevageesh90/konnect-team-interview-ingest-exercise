package com.kong.konnect.search.model;

/**
 * @author hegdevageesh
 *
 * CDC Event entity
 *
 * @param before - A field in the entity. To be interpreted.
 * @param after - A field in the entity. To be interpreted.
 * @param op - A field in the entity. To be interpreted.
 * @param tsMs - A field in the entity. To be interpreted.
 */
public record CDCEvent(
        Before before,
        After after,
        String op,
        long tsMs
) {
    public record Before() {}

    public record After(
            String key,
            Value value
    ) {
        public record Value(
                int type,
                Object object
        ) {}
    }
}
