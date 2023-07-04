package org.football.stats.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;
import org.football.stats.props.PropertiesLoader;
import org.football.stats.props.PropertiesSupplier;

public class ApiBuilder {

    public static void build() {
        RestAssured.requestSpecification = getApiSpecification();
        RestAssured.config = buildConfig();
    }

    private static void throwException(String property) {
        throw new RuntimeException("Property api.source set to - " + property + " , butm ust be set either as 'rapid.api' or 'api'sports'.");
    }

    public static RestAssuredConfig buildConfig() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.addMixIn(Object.class, IgnoreUnknownPropertiesMixin.class);

        // Configure Rest Assured to use the custom ObjectMapper
        return RestAssuredConfig.config().objectMapperConfig(ObjectMapperConfig.objectMapperConfig().jackson2ObjectMapperFactory(
                (cls, charset) -> objectMapper
        ));
    }

    // Mixin class to add @JsonIgnoreProperties(ignoreUnknown = true) to all classes
    @JsonIgnoreProperties(ignoreUnknown = true)
    private abstract static class IgnoreUnknownPropertiesMixin {
    }

    private static RequestSpecification getApiSpecification() {
        String property = PropertiesSupplier.getProperty("api.source");
        switch (property) {
            case "rapid.api":
                PropertiesLoader.loadGlobalProperties(ApiBuilder.class, "rapid.api.properties");
                return new RequestSpecBuilder()
                        .setBaseUri(PropertiesSupplier.getProperty("url"))
                        .addHeader("x-rapidapi-key", PropertiesSupplier.getProperty("secret.key"))
                        .addHeader("x-rapidapi-host", PropertiesSupplier.getProperty("rapid.api.host"))
                        .build();
            case "api.sports":
                PropertiesLoader.loadGlobalProperties(ApiBuilder.class, "api.sports.properties");
                return new RequestSpecBuilder()
                        .setBaseUri(PropertiesSupplier.getProperty("url"))
                        .addHeader("x-apisports-key", PropertiesSupplier.getProperty("secret.key"))
                        .build();
            default:
                throwException(property);
        }
        return null;
    }
}
