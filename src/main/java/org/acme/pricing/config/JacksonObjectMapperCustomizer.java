package org.acme.pricing.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonObjectMapperCustomizer implements Jackson2ObjectMapperBuilderCustomizer {

    /**
     * Customize the ObjectMapper globally.
     * - handle OffsetDateTime
     * - do not return null fields in JSON
     * - etc
     *
     */
    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
        builder.serializationInclusion(JsonInclude.Include.NON_NULL); // avoid "field_name": null in JSON response
        builder.featuresToDisable(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, // handle OffsetDateTime
                DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE // Z instead of +00:00 for UTC
        );
        builder.modulesToInstall(new JavaTimeModule());
    }
}