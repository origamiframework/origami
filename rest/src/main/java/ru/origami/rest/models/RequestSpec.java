package ru.origami.rest.models;

import io.restassured.response.Response;
import io.restassured.specification.*;

import java.net.URI;
import java.net.URL;
import java.util.Map;

import static io.restassured.RestAssured.given;

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
}
