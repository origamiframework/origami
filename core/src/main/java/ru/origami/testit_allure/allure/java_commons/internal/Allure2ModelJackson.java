/*
 *  Copyright 2016-2026 Qameta Software Inc
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package ru.origami.testit_allure.allure.java_commons.internal;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdSerializer;
import ru.origami.testit_allure.allure.model.Parameter;
import ru.origami.testit_allure.allure.model.Stage;
import ru.origami.testit_allure.allure.model.Status;

import java.util.Locale;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static tools.jackson.databind.MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME;
import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;

/**
 * The type Allure 2 model jackson.
 */
@SuppressWarnings("PMD.ClassNamingConventions")
public final class Allure2ModelJackson {

    public static final String INDENT_OUTPUT_PROPERTY_NAME =
            "allure.results.indentOutput";

    private Allure2ModelJackson() {
        throw new IllegalStateException("Do not instance Allure2ModelJackson");
    }

    public static ObjectMapper createMapper() {
        return JsonMapper.builder()
                .configure(USE_WRAPPER_NAME_AS_PROPERTY_NAME, true)
                .changeDefaultPropertyInclusion(
                        inclusion -> inclusion.withValueInclusion(NON_NULL))
                .changeDefaultPropertyInclusion(
                        inclusion -> inclusion.withContentInclusion(NON_NULL))
                .configure(
                        INDENT_OUTPUT,
                        Boolean.getBoolean(INDENT_OUTPUT_PROPERTY_NAME))
                .addModule(new SimpleModule()
                        .addSerializer(Status.class, new StatusSerializer())
                        .addSerializer(Stage.class, new StageSerializer())
                        .addSerializer(
                                Parameter.Mode.class,
                                new ParameterModeSerializer()))
                .build();
    }

    private static class ParameterModeSerializer
            extends StdSerializer<Parameter.Mode> {

        protected ParameterModeSerializer() {
            super(Parameter.Mode.class);
        }

        @Override
        public void serialize(
                final Parameter.Mode value,
                final JsonGenerator gen,
                final SerializationContext ctxt) throws JacksonException {
            gen.writeString(value.name().toLowerCase(Locale.ENGLISH));
        }
    }

    private static class StageSerializer extends StdSerializer<Stage> {

        protected StageSerializer() {
            super(Stage.class);
        }

        @Override
        public void serialize(
                final Stage value,
                final JsonGenerator gen,
                final SerializationContext ctxt) throws JacksonException {
            gen.writeString(value.name().toLowerCase(Locale.ENGLISH));
        }
    }

    private static class StatusSerializer extends StdSerializer<Status> {

        protected StatusSerializer() {
            super(Status.class);
        }

        @Override
        public void serialize(
                final Status value,
                final JsonGenerator gen,
                final SerializationContext ctxt) throws JacksonException {
            gen.writeString(value.name().toLowerCase(Locale.ENGLISH));
        }
    }
}
