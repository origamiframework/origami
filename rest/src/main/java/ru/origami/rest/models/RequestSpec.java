package ru.origami.rest.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.specification.*;
import ru.origami.rest.utils.PathTrackingFilter;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static ru.origami.rest.RestSteps.SKIP_FIELD_FILTER;

public class RequestSpec {

    private RequestSpecification spec;

    public RequestSpec(RequestSpecification spec) {
        this.spec = spec;
    }

    public Response get() {
        return given().spec(spec).get();
    }

    public Response post() {
        return given().spec(spec).post();
    }

    public Response put() {
        return given().spec(spec).put();
    }

    public Response delete() {
        return given().spec(spec).delete();
    }

    public Response head() {
        return given().spec(spec).head();
    }

    public Response patch() {
        return given().spec(spec).patch();
    }

    public Response options() {
        return given().spec(spec).options();
    }

    public Response get(URI var1) {
        return given().spec(spec).get(var1);
    }

    public Response post(URI var1) {
        return given().spec(spec).post(var1);
    }

    public Response put(URI var1) {
        return given().spec(spec).put(var1);
    }

    public Response delete(URI var1) {
        return given().spec(spec).delete(var1);
    }

    public Response head(URI var1) {
        return given().spec(spec).head(var1);
    }

    public Response patch(URI var1) {
        return given().spec(spec).patch(var1);
    }

    public Response options(URI var1) {
        return given().spec(spec).options(var1);
    }

    public Response get(URL var1) {
        return given().spec(spec).get(var1);
    }

    public Response post(URL var1) {
        return given().spec(spec).post(var1);
    }

    public Response put(URL var1) {
        return given().spec(spec).put(var1);
    }

    public Response delete(URL var1) {
        return given().spec(spec).delete(var1);
    }

    public Response head(URL var1) {
        return given().spec(spec).head(var1);
    }

    public Response patch(URL var1) {
        return given().spec(spec).patch(var1);
    }

    public Response options(URL var1) {
        return given().spec(spec).options(var1);
    }

    public Response get(String var1, Object... var2) {
        return given().spec(spec).get(var1, var2);
    }

    public Response get(String var1, Map<String, ?> var2) {
        return given().spec(spec).get(var1, var2);
    }

    public Response post(String var1, Object... var2) {
        return given().spec(spec).post(var1, var2);
    }

    public Response post(String var1, Map<String, ?> var2) {
        return given().spec(spec).post(var1, var2);
    }

    public Response put(String var1, Object... var2) {
        return given().spec(spec).put(var1, var2);
    }

    public Response put(String var1, Map<String, ?> var2) {
        return given().spec(spec).put(var1, var2);
    }

    public Response delete(String var1, Object... var2) {
        return given().spec(spec).delete(var1, var2);
    }

    public Response delete(String var1, Map<String, ?> var2) {
        return given().spec(spec).delete(var1, var2);
    }

    public Response head(String var1, Object... var2) {
        return given().spec(spec).head(var1, var2);
    }

    public Response head(String var1, Map<String, ?> var2) {
        return given().spec(spec).head(var1, var2);
    }

    public Response patch(String var1, Object... var2) {
        return given().spec(spec).patch(var1, var2);
    }

    public Response patch(String var1, Map<String, ?> var2) {
        return given().spec(spec).patch(var1, var2);
    }

    public Response options(String var1, Object... var2) {
        return given().spec(spec).options(var1, var2);
    }

    public Response options(String var1, Map<String, ?> var2) {
        return given().spec(spec).options(var1, var2);
    }

    public RequestSpec withSkipFields(String... fieldsToSkip) {
        Set<String> skipSet = new HashSet<>(Arrays.asList(fieldsToSkip));
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setFilterProvider(new SimpleFilterProvider().addFilter(SKIP_FIELD_FILTER, new PathTrackingFilter(skipSet)));
        RestAssuredConfig config = RestAssuredConfig.config()
                .objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory((type, s) -> mapper));

        return new RequestSpecBuilder(spec)
                .setConfig(config)
                .build();
    }
}
