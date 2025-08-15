package ru.origami.rest;

import io.restassured.filter.FilterContext;
import io.restassured.filter.OrderedFilter;
import io.restassured.internal.NameAndValue;
import io.restassured.internal.RestAssuredResponseImpl;
import io.restassured.internal.RestAssuredResponseOptionsGroovyImpl;
import io.restassured.internal.support.Prettifier;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.MultiPartSpecification;
import lombok.extern.slf4j.Slf4j;
import ru.origami.rest.attachment.RequestAttachment;
import ru.origami.rest.attachment.ResponseAttachment;
import ru.origami.rest.attachment.RestAssuredAttachmentProcessor;
import ru.origami.rest.attachment.RestAssuredAttachmentRenderer;
import ru.origami.rest.attachment.bytes.ResponseBytesAttachment;
import ru.origami.rest.attachment.bytes.RestAssuredBytesAttachmentProcessor;
import ru.origami.rest.attachment.bytes.RestAssuredBytesAttachmentRenderer;
import ru.origami.rest.models.EContentType;
import ru.origami.testit_allure.annotations.Description;
import ru.origami.testit_allure.annotations.Step;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.origami.common.environment.Environment.*;
import static ru.origami.rest.attachment.RequestAttachment.Builder.create;
import static ru.origami.testit_allure.allure.java_commons.Allure.getLifecycle;
import static ru.origami.testit_allure.test_it.testit.aspects.StepAspect.TEST_IT_ATTACHMENT_TECH_STEP_VALUE;

@Slf4j
public class AllureRestAssured implements OrderedFilter {

    private static final String REQUEST_ATTACHMENT_NAME_CURL = "request.curl";
    private static final String REQUEST_ATTACHMENT_NAME_HEADERS = "request.headers";
    private static final String REQUEST_ATTACHMENT_NAME_COOKIES = "request.cookies";
    private static final String REQUEST_ATTACHMENT_NAME_BODY = "request.body";
    private static final String RESPONSE_ATTACHMENT_NAME_BODY = "response.body";
    private static final String RESPONSE_ATTACHMENT_NAME_HEADERS = "response.headers";
    private static final String RESPONSE_ATTACHMENT_NAME_DATA = "response.data";

    private static final Prettifier prettifier = new Prettifier();

    private Response response;

    private static Set<EContentType> contentTypes = EContentType.getContentTypesWithByteArrayResp();

    public Response getResponse() {
        return response;
    }

    @Override
    public Response filter(final FilterableRequestSpecification requestSpec,
                           final FilterableResponseSpecification responseSpec,
                           final FilterContext filterContext) {
        final String url = requestSpec.getURI();

        final String requestStep = String.format("%s %s", requestSpec.getMethod(), url);
        createRequest(requestStep, requestSpec);

        final Response response = filterContext.next(requestSpec, responseSpec);
        final String responseStep = String.format("Response -> %s", response.getStatusLine());
        createResponse(responseStep, response);

        return response;
    }

    private static Map<String, String> toMapConverter(final Iterable<? extends NameAndValue> items) {
        final Map<String, String> result = new HashMap<>();
        items.forEach(h -> result.put(h.getName(), h.getValue()));

        return result;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Step("{logStep}")
    protected void createRequest(String logStep, final FilterableRequestSpecification requestSpec) {
        getLifecycle().updateStep(step -> {
            step.getParameters().remove(0);
        });

        final String url = requestSpec.getURI();

        // Add request curl attachment
        final RequestAttachment.Builder requestCurlBuilder = create(REQUEST_ATTACHMENT_NAME_CURL, url)
                .setMethod(requestSpec.getMethod())
                .setHeaders(toMapConverter(requestSpec.getHeaders()))
                .setCookies(toMapConverter(requestSpec.getCookies()));

        if (Objects.nonNull(requestSpec.getBody())) {
            requestCurlBuilder.setBody(prettifier.getPrettifiedBodyIfPossible(requestSpec));
        }

        // add multipart
        requestCurlBuilder.setMultipartFormData(requestSpec.getMultiPartParams());

        RequestAttachment requestCurl = requestCurlBuilder.build();

        new RestAssuredAttachmentProcessor().addAttachment(requestCurl, new RestAssuredAttachmentRenderer());
        attachDescriptionToTestIT(requestCurl.getCurl());

        // Add request headers attachment
        if (!toMapConverter(requestSpec.getHeaders()).isEmpty()) {
            final RequestAttachment requestHeaders = create(REQUEST_ATTACHMENT_NAME_HEADERS)
                    .setHeaders(toMapConverter(requestSpec.getHeaders()))
                    .build();

            new RestAssuredAttachmentProcessor().addAttachment(requestHeaders, new RestAssuredAttachmentRenderer());
        }

        // Add request cookies attachment
        if (!toMapConverter(requestSpec.getCookies()).isEmpty()) {
            final RequestAttachment requestCookies = create(REQUEST_ATTACHMENT_NAME_COOKIES)
                    .setCookies(toMapConverter(requestSpec.getCookies()))
                    .build();

            new RestAssuredAttachmentProcessor().addAttachment(requestCookies, new RestAssuredAttachmentRenderer());
        }

        // Add request body attachment
        if (Objects.nonNull(requestSpec.getBody())) {
            final RequestAttachment requestBody = create(REQUEST_ATTACHMENT_NAME_BODY)
                    .setBody(prettifier.getPrettifiedBodyIfPossible(requestSpec))
                    .setContentType(requestSpec.getContentType())
                    .build();

            new RestAssuredAttachmentProcessor().addAttachment(requestBody, new RestAssuredAttachmentRenderer());
        }

        // Add request file attachment
        if (requestSpec.getContentType().startsWith("multipart/form-data")) {
            if (Objects.nonNull(requestSpec.getMultiPartParams())) {
                for (MultiPartSpecification spec : requestSpec.getMultiPartParams()) {
                    if (spec.getContent() instanceof File file) {
                        try (FileInputStream fis = new FileInputStream(file)) {
                            byte[] fileByteArray = new byte[(int) file.length()];
                            fis.read(fileByteArray);
                            String fileExtension = spec.getFileName().substring(spec.getFileName().lastIndexOf(".") + 1);
                            final ResponseBytesAttachment requestFile = ResponseBytesAttachment.Builder
                                    .create(spec.getFileName())
                                    .setBody(fileByteArray)
                                    .setContentType(EContentType.getContentTypeByExtension(fileExtension).getContentType())
                                    .build();

                            new RestAssuredBytesAttachmentProcessor().addAttachment(requestFile, new RestAssuredBytesAttachmentRenderer());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Step("{logStep}")
    protected void createResponse(String logStep, final Response response) {
        getLifecycle().updateStep(step -> {
            step.getParameters().remove(0);
        });

        Predicate<String> isByteContentType = (String contentType) -> contentTypes.stream()
                .map(EContentType::getContentType)
                .collect(Collectors.toList())
                .contains(contentType);
        String fileName = null;

        if (Objects.nonNull(response.getBody())) {
            if (isByteContentType.test(response.getContentType())) {
                try {
                    String contentDisposition = URLDecoder.decode(response.getHeader("Content-Disposition"), StandardCharsets.UTF_8);
                    String[] attributes = contentDisposition.split(";");

                    for (String a : attributes) {
                        if (a.toLowerCase().contains("filename")) {
                            if (a.indexOf("UTF-8''") > 0) {
                                fileName = a.substring(a.indexOf("UTF-8''") + 7);
                            } else {
                                fileName = a.substring(a.indexOf('=') + 1).replaceAll("^\"|\"$", "");
                            }
                        }
                    }
                } catch (Exception e) {
                }

                final ResponseBytesAttachment responseBody = ResponseBytesAttachment.Builder
                        .create(Objects.isNull(fileName) ? RESPONSE_ATTACHMENT_NAME_DATA : fileName)
                        .setBody(response.getBody().asByteArray())
                        .setContentType(response.getContentType())
                        .build();

                new RestAssuredBytesAttachmentProcessor().addAttachment(responseBody, new RestAssuredBytesAttachmentRenderer());
            } else {
                final ResponseAttachment responseBody = ResponseAttachment.Builder
                        .create(RESPONSE_ATTACHMENT_NAME_BODY)
                        .setBody(prettifier.getPrettifiedBodyIfPossible(response, response.getBody()))
                        .build();

                new RestAssuredAttachmentProcessor().addAttachment(responseBody, new RestAssuredAttachmentRenderer());
            }
        }

        // Add response headers attachment
        if (!toMapConverter(response.getHeaders()).isEmpty()) {
            final ResponseAttachment responseHeaders = ResponseAttachment.Builder
                    .create(RESPONSE_ATTACHMENT_NAME_HEADERS)
                    .setHeaders(toMapConverter(response.getHeaders()))
                    .build();

            new RestAssuredAttachmentProcessor().addAttachment(responseHeaders, new RestAssuredAttachmentRenderer());
        }

        this.response = response;

        if (!isByteContentType.test(response.getContentType())) {
            if (Objects.isNull(response.asPrettyString()) || response.asPrettyString().isEmpty()) {
                attachDescriptionToTestIT("Empty response body");
                printResponse(response, "<none>");
            } else {
                attachDescriptionToTestIT(response.asPrettyString());
                printResponse(response, String.format("\n%s", response.asPrettyString()));
            }
        } else {
            String attach = String.format("File attachment.\n\t   File name: %s\n\t   Content-type: %s", fileName, response.getContentType());
            RestAssuredResponseImpl responsePrintBody = new RestAssuredResponseImpl();
            RestAssuredResponseOptionsGroovyImpl groovyResponse = new RestAssuredResponseOptionsGroovyImpl();
            groovyResponse.setResponseHeaders(response.getHeaders());
            groovyResponse.setCookies(response.getDetailedCookies());
            groovyResponse.setStatusCode(response.getStatusCode());
            groovyResponse.setStatusLine(response.getStatusLine());
            groovyResponse.setContent(attach);
            responsePrintBody.setGroovyResponse(groovyResponse);
            printResponse(responsePrintBody, attach);
            attachDescriptionToTestIT(attach);
        }
    }

    private void printResponse(Response response, String body) {
        if (isLocal() || isLoggingEnabled()) {
            log.info("\nResponse: {}\nHeaders:  {}\nCookies:  {}\nBody:  {}\n", response.getStatusLine(),
                    response.getHeaders().size() == 0 ? "<none>" : response.getHeaders().asList().stream()
                            .map(h -> String.format("%s: %s", h.getName(), h.getValue()))
                            .collect(Collectors.joining("\n\t\t  ")),
                    response.getCookies().size() == 0 ? "<none>" : response.getCookies().entrySet().stream()
                            .map(c -> String.format("%s: %s", c.getKey(), c.getValue()))
                            .collect(Collectors.joining("\n\t\t  ")),
                    body);
        }
    }

    @Step(TEST_IT_ATTACHMENT_TECH_STEP_VALUE)
    @Description("{0}")
    private void attachDescriptionToTestIT(String value) {
    }
}
