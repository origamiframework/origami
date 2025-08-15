package ru.origami.rest.models;

import io.restassured.authentication.AuthenticationScheme;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.ProxySpecification;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import lombok.ToString;
import ru.origami.rest.AllureRestAssured;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.util.*;

import static ru.origami.common.OrigamiHelper.getObjectAsXmlString;
import static ru.origami.common.environment.Environment.SSL_VERIFICATION;
import static ru.origami.common.environment.Environment.getWithNullValue;

@Getter
@ToString
public class RequestSpecBuilder {

    private io.restassured.builder.RequestSpecBuilder requestSpecBuilder;

    public RequestSpecBuilder(AllureRestAssured allureRestAssured) {
        requestSpecBuilder = new io.restassured.builder.RequestSpecBuilder().addFilter(allureRestAssured);
    }

    public RequestSpecBuilder setBody(String body) {
        requestSpecBuilder.setBody(body);

        return this;
    }

    public RequestSpecBuilder setNotNullBody(String body) {
        if (Objects.nonNull(body)) {
            requestSpecBuilder.setBody(body);
        }

        return this;
    }

    public RequestSpecBuilder setBody(byte[] body) {
        requestSpecBuilder.setBody(body);

        return this;
    }

    public RequestSpecBuilder setNotNullBody(byte[] body) {
        if (Objects.nonNull(body)) {
            requestSpecBuilder.setBody(body);
        }

        return this;
    }

    public RequestSpecBuilder setBody(Object object) {
        requestSpecBuilder.setBody(object);

        return this;
    }

    public RequestSpecBuilder setNotNullBody(Object object) {
        if (Objects.nonNull(object)) {
            requestSpecBuilder.setBody(object);
        }

        return this;
    }

    public RequestSpecBuilder setBody(Object object, ObjectMapper mapper) {
        requestSpecBuilder.setBody(object, mapper);

        return this;
    }

    public RequestSpecBuilder setNotNullBody(Object object, ObjectMapper mapper) {
        if (Objects.nonNull(object)) {
            requestSpecBuilder.setBody(object, mapper);
        }

        return this;
    }

    public RequestSpecBuilder setBody(Object object, ObjectMapperType mapperType) {
        if (mapperType == ObjectMapperType.JAXB) {
            requestSpecBuilder.setBody(getObjectAsXmlString(object));
        } else {
            requestSpecBuilder.setBody(object, mapperType);
        }

        return this;
    }

    public RequestSpecBuilder setNotNullBody(Object object, ObjectMapperType mapperType) {
        if (Objects.nonNull(object)) {
            requestSpecBuilder.setBody(object, mapperType);
        }

        return this;
    }

    public RequestSpecBuilder addCookies(Map<String, ?> cookies) {
        requestSpecBuilder.addCookies(cookies);

        return this;
    }

    public RequestSpecBuilder addNotNullCookies(Map<String, ?> cookies) {
        if (Objects.nonNull(cookies)) {
            requestSpecBuilder.addCookies(cookies);
        }

        return this;
    }

    public RequestSpecBuilder addCookie(Cookie cookie) {
        requestSpecBuilder.addCookie(cookie);

        return this;
    }

    public RequestSpecBuilder addNotNullCookie(Cookie cookie) {
        if (Objects.nonNull(cookie)) {
            requestSpecBuilder.addCookie(cookie);
        }

        return this;
    }

    public RequestSpecBuilder addCookie(String key, Object value, Object... cookieNameValuePairs) {
        requestSpecBuilder.addCookie(key, value, cookieNameValuePairs);

        return this;
    }

    public RequestSpecBuilder addNotNullCookie(String key, Object value, Object... cookieNameValuePairs) {
        if (Objects.nonNull(value)) {
            requestSpecBuilder.addCookie(key, value, cookieNameValuePairs);
        }

        return this;
    }

    public RequestSpecBuilder addCookie(String name) {
        requestSpecBuilder.addCookie(name);

        return this;
    }

    public RequestSpecBuilder addCookies(Cookies cookies) {
        requestSpecBuilder.addCookies(cookies);

        return this;
    }

    public RequestSpecBuilder addNotNullCookies(Cookies cookies) {
        if (Objects.nonNull(cookies)) {
            requestSpecBuilder.addCookies(cookies);
        }

        return this;
    }

    public RequestSpecBuilder addFilter(Filter filter) {
        requestSpecBuilder.addFilter(filter);

        return this;
    }

    public RequestSpecBuilder addNotNullFilter(Filter filter) {
        if (Objects.nonNull(filter)) {
            requestSpecBuilder.addFilter(filter);
        }

        return this;
    }

    public RequestSpecBuilder addFilters(List<Filter> filters) {
        requestSpecBuilder.addFilters(filters);

        return this;
    }

    public RequestSpecBuilder addNotNullFilters(List<Filter> filters) {
        if (Objects.nonNull(filters)) {
            requestSpecBuilder.addFilters(filters);
        }

        return this;
    }

    public RequestSpecBuilder addParams(Map<String, ?> parametersMap) {
        requestSpecBuilder.addParams(parametersMap);

        return this;
    }

    public RequestSpecBuilder addNotNullParams(Map<String, ?> parametersMap) {
        if (Objects.nonNull(parametersMap)) {
            requestSpecBuilder.addParams(parametersMap);
        }

        return this;
    }

    public RequestSpecBuilder addParam(String parameterName, Object... parameterValues) {
        requestSpecBuilder.addParam(parameterName, parameterValues);

        return this;
    }

    public RequestSpecBuilder addNotNullParam(String parameterName, Object... parameterValues) {
        if (Objects.nonNull(parameterValues) && parameterValues.length > 0 && parameterValues[0] != null) {
            requestSpecBuilder.addParam(parameterName, parameterValues);
        }

        return this;
    }

    public RequestSpecBuilder addParam(String parameterName, Collection<?> parameterValues) {
        requestSpecBuilder.addParam(parameterName, parameterValues);

        return this;
    }

    public RequestSpecBuilder addNotNullParam(String parameterName, Collection<?> parameterValues) {
        if (Objects.nonNull(parameterValues)) {
            requestSpecBuilder.addParam(parameterName, parameterValues);
        }

        return this;
    }

    public RequestSpecBuilder removeParam(String parameterName) {
        requestSpecBuilder.removeParam(parameterName);

        return this;
    }

    public RequestSpecBuilder addQueryParam(String parameterName, Collection<?> parameterValues) {
        requestSpecBuilder.addQueryParam(parameterName, parameterValues);

        return this;
    }

    public RequestSpecBuilder addNotNullQueryParam(String parameterName, Collection<?> parameterValues) {
        if (Objects.nonNull(parameterValues)) {
            requestSpecBuilder.addQueryParam(parameterName, parameterValues);
        }

        return this;
    }

    public RequestSpecBuilder addQueryParams(Map<String, ?> parametersMap) {
        requestSpecBuilder.addQueryParams(parametersMap);

        return this;
    }

    public RequestSpecBuilder addNotNullQueryParams(Map<String, ?> parametersMap) {
        if (Objects.nonNull(parametersMap)) {
            requestSpecBuilder.addQueryParams(parametersMap);
        }

        return this;
    }

    public RequestSpecBuilder addQueryParam(String parameterName, Object... parameterValues) {
        requestSpecBuilder.addQueryParam(parameterName, parameterValues);

        return this;
    }

    public RequestSpecBuilder addNotNullQueryParam(String parameterName, Object... parameterValues) {
        if (Objects.nonNull(parameterValues) && parameterValues.length > 0 && parameterValues[0] != null) {
            requestSpecBuilder.addQueryParam(parameterName, parameterValues);
        }

        return this;
    }

    public RequestSpecBuilder removeQueryParam(String parameterName) {
        requestSpecBuilder.removeQueryParam(parameterName);

        return this;
    }

    public RequestSpecBuilder addFormParam(String parameterName, Collection<?> parameterValues) {
        requestSpecBuilder.addFormParam(parameterName, parameterValues);

        return this;
    }

    public RequestSpecBuilder addNotNullFormParam(String parameterName, Collection<?> parameterValues) {
        if (Objects.nonNull(parameterValues)) {
            requestSpecBuilder.addFormParam(parameterName, parameterValues);
        }

        return this;
    }

    public RequestSpecBuilder addFormParams(Map<String, ?> parametersMap) {
        requestSpecBuilder.addFormParams(parametersMap);

        return this;
    }

    public RequestSpecBuilder addNotNullFormParams(Map<String, ?> parametersMap) {
        if (Objects.nonNull(parametersMap)) {
            requestSpecBuilder.addFormParams(parametersMap);
        }

        return this;
    }

    public RequestSpecBuilder addFormParam(String parameterName, Object... parameterValues) {
        requestSpecBuilder.addFormParam(parameterName, parameterValues);

        return this;
    }

    public RequestSpecBuilder addNotNullFormParam(String parameterName, Object... parameterValues) {
        if (Objects.nonNull(parameterValues) && parameterValues.length > 0 && parameterValues[0] != null) {
            requestSpecBuilder.addFormParam(parameterName, parameterValues);
        }

        return this;
    }

    public RequestSpecBuilder removeFormParam(String parameterName) {
        requestSpecBuilder.removeFormParam(parameterName);

        return this;
    }

    public RequestSpecBuilder addPathParam(String parameterName, Object parameterValue) {
        requestSpecBuilder.addPathParam(parameterName, parameterValue);

        return this;
    }

    public RequestSpecBuilder addNotNullPathParam(String parameterName, Object parameterValue) {
        if (Objects.nonNull(parameterValue)) {
            requestSpecBuilder.addPathParam(parameterName, parameterValue);
        }

        return this;
    }

    public RequestSpecBuilder addPathParams(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        requestSpecBuilder.addPathParams(firstParameterName, firstParameterValue, parameterNameValuePairs);

        return this;
    }

    public RequestSpecBuilder addNotNullPathParams(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        if (Objects.nonNull(firstParameterValue)) {
            requestSpecBuilder.addPathParams(firstParameterName, firstParameterValue, parameterNameValuePairs);
        }

        return this;
    }

    public RequestSpecBuilder addPathParams(Map<String, ?> parameterNameValuePairs) {
        requestSpecBuilder.addPathParams(parameterNameValuePairs);

        return this;
    }

    public RequestSpecBuilder addNotNullPathParams(Map<String, ?> parameterNameValuePairs) {
        if (Objects.nonNull(parameterNameValuePairs)) {
            requestSpecBuilder.addPathParams(parameterNameValuePairs);
        }

        return this;
    }

    public RequestSpecBuilder removePathParam(String parameterName) {
        requestSpecBuilder.removePathParam(parameterName);

        return this;
    }

    public RequestSpecBuilder setKeyStore(String pathToJks, String password) {
        requestSpecBuilder.setKeyStore(pathToJks, password);

        return this;
    }

    public RequestSpecBuilder setTrustStore(String pathToJks, String password) {
        requestSpecBuilder.setTrustStore(pathToJks, password);

        return this;
    }

    public RequestSpecBuilder setTrustStore(File pathToJks, String password) {
        requestSpecBuilder.setTrustStore(pathToJks, password);

        return this;
    }

    public RequestSpecBuilder addHeaders(Map<String, String> headers) {
        requestSpecBuilder.addHeaders(headers);

        return this;
    }

    public RequestSpecBuilder addNotNullHeaders(Map<String, String> headers) {
        if (Objects.nonNull(headers)) {
            requestSpecBuilder.addHeaders(headers);
        }

        return this;
    }

    public RequestSpecBuilder addHeader(String headerName, String headerValue) {
        requestSpecBuilder.addHeader(headerName, headerValue);

        return this;
    }

    public RequestSpecBuilder addNotNullHeader(String headerName, String headerValue) {
        if (Objects.nonNull(headerValue)) {
            requestSpecBuilder.addHeader(headerName, headerValue);
        }

        return this;
    }

    public RequestSpecBuilder setContentType(ContentType contentType) {
        requestSpecBuilder.setContentType(contentType);

        return this;
    }

    public RequestSpecBuilder setNotNullContentType(ContentType contentType) {
        if (Objects.nonNull(contentType)) {
            requestSpecBuilder.setContentType(contentType);
        }

        return this;
    }

    public RequestSpecBuilder setContentType(String contentType) {
        requestSpecBuilder.setContentType(contentType);

        return this;
    }

    public RequestSpecBuilder setNotNullContentType(String contentType) {
        if (Objects.nonNull(contentType)) {
            requestSpecBuilder.setContentType(contentType);
        }

        return this;
    }

    public RequestSpecBuilder noContentType() {
        requestSpecBuilder.noContentType();

        return this;
    }

    public RequestSpecBuilder disableCsrf() {
        requestSpecBuilder.disableCsrf();

        return this;
    }

    public RequestSpecBuilder setAccept(ContentType contentType) {
        requestSpecBuilder.setAccept(contentType);

        return this;
    }

    public RequestSpecBuilder setNotNullAccept(ContentType contentType) {
        if (Objects.nonNull(contentType)) {
            requestSpecBuilder.setAccept(contentType);
        }

        return this;
    }

    public RequestSpecBuilder setAccept(String mediaTypes) {
        requestSpecBuilder.setAccept(mediaTypes);

        return this;
    }

    public RequestSpecBuilder setNotNullAccept(String mediaTypes) {
        if (Objects.nonNull(mediaTypes)) {
            requestSpecBuilder.setAccept(mediaTypes);
        }

        return this;
    }

    public RequestSpecBuilder addMultiPart(MultiPartSpecification multiPartSpecification) {
        requestSpecBuilder.addMultiPart(multiPartSpecification);

        return this;
    }

    public RequestSpecBuilder addNotNullMultiPart(MultiPartSpecification multiPartSpecification) {
        if (Objects.nonNull(multiPartSpecification)) {
            requestSpecBuilder.addMultiPart(multiPartSpecification);
        }

        return this;
    }

    public RequestSpecBuilder addMultiPart(File file) {
        requestSpecBuilder.addMultiPart(file);

        return this;
    }

    public RequestSpecBuilder addNotNullMultiPart(File file) {
        if (Objects.nonNull(file)) {
            requestSpecBuilder.addMultiPart(file);
        }

        return this;
    }

    public RequestSpecBuilder addMultiPart(String controlName, File file) {
        requestSpecBuilder.addMultiPart(controlName, file);

        return this;
    }

    public RequestSpecBuilder addNotNullMultiPart(String controlName, File file) {
        if (Objects.nonNull(file)) {
            requestSpecBuilder.addMultiPart(controlName, file);
        }

        return this;
    }

    public RequestSpecBuilder addMultiPart(String controlName, File file, String mimeType) {
        requestSpecBuilder.addMultiPart(controlName, file, mimeType);

        return this;
    }

    public RequestSpecBuilder addNotNullMultiPart(String controlName, File file, String mimeType) {
        if (Objects.nonNull(file)) {
            requestSpecBuilder.addMultiPart(controlName, file, mimeType);
        }

        return this;
    }

    public RequestSpecBuilder addMultiPart(String controlName, String fileName, byte[] bytes) {
        requestSpecBuilder.addMultiPart(controlName, fileName, bytes);

        return this;
    }

    public RequestSpecBuilder addNotNullMultiPart(String controlName, String fileName, byte[] bytes) {
        if (Objects.nonNull(bytes)) {
            requestSpecBuilder.addMultiPart(controlName, fileName, bytes);
        }

        return this;
    }

    public RequestSpecBuilder addMultiPart(String controlName, String fileName, byte[] bytes, String mimeType) {
        requestSpecBuilder.addMultiPart(controlName, fileName, bytes, mimeType);

        return this;
    }

    public RequestSpecBuilder addNotNullMultiPart(String controlName, String fileName, byte[] bytes, String mimeType) {
        if (Objects.nonNull(bytes)) {
            requestSpecBuilder.addMultiPart(controlName, fileName, bytes, mimeType);
        }

        return this;
    }

    public RequestSpecBuilder addMultiPart(String controlName, String fileName, InputStream stream) {
        requestSpecBuilder.addMultiPart(controlName, fileName, stream);

        return this;
    }

    public RequestSpecBuilder addNotNullMultiPart(String controlName, String fileName, InputStream stream) {
        if (Objects.nonNull(stream)) {
            requestSpecBuilder.addMultiPart(controlName, fileName, stream);
        }

        return this;
    }

    public RequestSpecBuilder addMultiPart(String controlName, String fileName, InputStream stream, String mimeType) {
        requestSpecBuilder.addMultiPart(controlName, fileName, stream, mimeType);

        return this;
    }

    public RequestSpecBuilder addNotNullMultiPart(String controlName, String fileName, InputStream stream, String mimeType) {
        if (Objects.nonNull(stream)) {
            requestSpecBuilder.addMultiPart(controlName, fileName, stream, mimeType);
        }

        return this;
    }

    public RequestSpecBuilder addMultiPart(String controlName, String contentBody) {
        requestSpecBuilder.addMultiPart(controlName, contentBody);

        return this;
    }

    public RequestSpecBuilder addNotNullMultiPart(String controlName, String contentBody) {
        if (Objects.nonNull(contentBody)) {
            requestSpecBuilder.addMultiPart(controlName, contentBody);
        }

        return this;
    }

    public RequestSpecBuilder addMultiPart(String controlName, String contentBody, String mimeType) {
        requestSpecBuilder.addMultiPart(controlName, contentBody, mimeType);

        return this;
    }

    public RequestSpecBuilder addNotNullMultiPart(String controlName, String contentBody, String mimeType) {
        if (Objects.nonNull(contentBody)) {
            requestSpecBuilder.addMultiPart(controlName, contentBody, mimeType);
        }

        return this;
    }

    public RequestSpecBuilder setAuth(AuthenticationScheme auth) {
        requestSpecBuilder.setAuth(auth);

        return this;
    }

    public RequestSpecBuilder setNotNullAuth(AuthenticationScheme auth) {
        if (Objects.nonNull(auth)) {
            requestSpecBuilder.setAuth(auth);
        }

        return this;
    }

    public RequestSpecBuilder setPort(int port) {
        requestSpecBuilder.setPort(port);

        return this;
    }

    public RequestSpecBuilder setNotNullPort(Integer port) {
        if (Objects.nonNull(port)) {
            requestSpecBuilder.setPort(port);
        }

        return this;
    }

    public RequestSpecBuilder setUrlEncodingEnabled(boolean isEnabled) {
        requestSpecBuilder.setUrlEncodingEnabled(isEnabled);

        return this;
    }

    public RequestSpecBuilder setSessionId(String sessionIdValue) {
        requestSpecBuilder.setSessionId(sessionIdValue);

        return this;
    }

    public RequestSpecBuilder setNotNullSessionId(String sessionIdValue) {
        if (Objects.nonNull(sessionIdValue)) {
            requestSpecBuilder.setSessionId(sessionIdValue);
        }

        return this;
    }

    public RequestSpecBuilder setSessionId(String sessionIdName, String sessionIdValue) {
        requestSpecBuilder.setSessionId(sessionIdName, sessionIdValue);

        return this;
    }

    public RequestSpecBuilder setNotNullSessionId(String sessionIdName, String sessionIdValue) {
        if (Objects.nonNull(sessionIdValue)) {
            requestSpecBuilder.setSessionId(sessionIdName, sessionIdValue);
        }

        return this;
    }

    public RequestSpecBuilder addRequestSpecification(RequestSpecification specification) {
        requestSpecBuilder.addRequestSpecification(specification);

        return this;
    }

    public RequestSpecBuilder addNotNullRequestSpecification(RequestSpecification specification) {
        if (Objects.nonNull(specification)) {
            requestSpecBuilder.addRequestSpecification(specification);
        }

        return this;
    }

    public RequestSpecBuilder setConfig(RestAssuredConfig config) {
        requestSpecBuilder.setConfig(config);

        return this;
    }

    public RequestSpecBuilder setNotNullConfig(RestAssuredConfig config) {
        if (Objects.nonNull(config)) {
            requestSpecBuilder.setConfig(config);
        }

        return this;
    }

    public RequestSpec build() {
        if ("true".equals(getWithNullValue(SSL_VERIFICATION))) {
            requestSpecBuilder.setRelaxedHTTPSValidation();
        }

        return new RequestSpec(requestSpecBuilder.build());
    }

    public RequestSpecBuilder setBaseUri(String uri) {
        requestSpecBuilder.setBaseUri(uri);

        return this;
    }

    public RequestSpecBuilder setBaseUri(URI uri) {
        requestSpecBuilder.setBaseUri(uri);

        return this;
    }

    public RequestSpecBuilder setBasePath(String path) {
        requestSpecBuilder.setBasePath(path);

        return this;
    }

    public RequestSpecBuilder log(LogDetail logDetail) {
        requestSpecBuilder.log(logDetail);

        return this;
    }

    public RequestSpecBuilder setTrustStore(KeyStore trustStore) {
        requestSpecBuilder.setTrustStore(trustStore);

        return this;
    }

    public RequestSpecBuilder setNotNullTrustStore(KeyStore trustStore) {
        if (Objects.nonNull(trustStore)) {
            requestSpecBuilder.setTrustStore(trustStore);
        }

        return this;
    }

    public RequestSpecBuilder setKeyStore(KeyStore keyStore) {
        requestSpecBuilder.setKeyStore(keyStore);

        return this;
    }

    public RequestSpecBuilder setNotNullKeyStore(KeyStore keyStore) {
        if (Objects.nonNull(keyStore)) {
            requestSpecBuilder.setKeyStore(keyStore);
        }

        return this;
    }

    public RequestSpecBuilder setRelaxedHTTPSValidation() {
        requestSpecBuilder.setRelaxedHTTPSValidation();

        return this;
    }

    public RequestSpecBuilder setRelaxedHTTPSValidation(String protocol) {
        requestSpecBuilder.setRelaxedHTTPSValidation(protocol);

        return this;
    }

    public RequestSpecBuilder setNotNullRelaxedHTTPSValidation(String protocol) {
        if (Objects.nonNull(protocol)) {
            requestSpecBuilder.setRelaxedHTTPSValidation(protocol);
        }

        return this;
    }

    public RequestSpecBuilder setProxy(String host, int port) {
        requestSpecBuilder.setProxy(host, port);

        return this;
    }

    public RequestSpecBuilder setNotNullProxy(String host, int port) {
        if (Objects.nonNull(host)) {
            requestSpecBuilder.setProxy(host, port);
        }

        return this;
    }

    public RequestSpecBuilder setProxy(String host) {
        requestSpecBuilder.setProxy(host);

        return this;
    }

    public RequestSpecBuilder setNotNullProxy(String host) {
        if (Objects.nonNull(host)) {
            requestSpecBuilder.setProxy(host);
        }

        return this;
    }

    public RequestSpecBuilder setProxy(int port) {
        requestSpecBuilder.setProxy(port);

        return this;
    }

    public RequestSpecBuilder setNotNullProxy(Integer port) {
        if (Objects.nonNull(port)) {
            requestSpecBuilder.setProxy(port);
        }

        return this;
    }

    public RequestSpecBuilder setProxy(String host, int port, String scheme) {
        requestSpecBuilder.setProxy(host, port, scheme);

        return this;
    }

    public RequestSpecBuilder setNotNullProxy(String host, int port, String scheme) {
        if (Objects.nonNull(host)) {
            requestSpecBuilder.setProxy(host, port, scheme);
        }

        return this;
    }

    public RequestSpecBuilder setProxy(URI uri) {
        requestSpecBuilder.setProxy(uri);

        return this;
    }

    public RequestSpecBuilder setNotNullProxy(URI uri) {
        if (Objects.nonNull(uri)) {
            requestSpecBuilder.setProxy(uri);
        }

        return this;
    }

    public RequestSpecBuilder setProxy(ProxySpecification proxySpecification) {
        requestSpecBuilder.setProxy(proxySpecification);

        return this;
    }

    public RequestSpecBuilder setNotNullProxy(ProxySpecification proxySpecification) {
        if (Objects.nonNull(proxySpecification)) {
            requestSpecBuilder.setProxy(proxySpecification);
        }

        return this;
    }

    public RequestSpecBuilder and() {
        return this;
    }
}
