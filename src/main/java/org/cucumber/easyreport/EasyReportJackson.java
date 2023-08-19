package org.cucumber.easyreport;

import io.cucumber.core.internal.com.fasterxml.jackson.annotation.JsonInclude;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonGenerator;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.DeserializationFeature;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.SerializationFeature;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.cfg.ConstructorDetector;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.json.JsonMapper;
import io.cucumber.core.internal.com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

final class EasyReportJackson {
    public static final ObjectMapper OBJECT_MAPPER;

    private EasyReportJackson() {
    }

    static {
        OBJECT_MAPPER =
                (
                        (JsonMapper.Builder)(
                                (JsonMapper.Builder)(
                                        (JsonMapper.Builder)(
                                                (JsonMapper.Builder)(
                                                        (JsonMapper.Builder)(
                                                                (JsonMapper.Builder)(
                                                                        (JsonMapper.Builder)(
                                                                                (JsonMapper.Builder)JsonMapper.builder().addModule(new Jdk8Module())
                                                                                            ).serializationInclusion(JsonInclude.Include.NON_ABSENT)
                                                                                    ).constructorDetector(ConstructorDetector.USE_PROPERTIES_BASED)
                                                                            ).enable(new SerializationFeature[]{SerializationFeature.WRITE_ENUMS_USING_TO_STRING})
                                                                    ).enable(new DeserializationFeature[]{DeserializationFeature.READ_ENUMS_USING_TO_STRING})
                                                            ).enable(new DeserializationFeature[]{DeserializationFeature.USE_LONG_FOR_INTS})
                                                    ).disable(new DeserializationFeature[]{DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES})
                                            ).disable(new JsonGenerator.Feature[]{JsonGenerator.Feature.AUTO_CLOSE_TARGET})
                ).build();
    }
}