package ru.origami.rest.utils;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.PropertyWriter;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;

import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.getLangValue;

public class PathTrackingFilter extends SimpleBeanPropertyFilter {

    private final Set<String> pathsToSkip;

    private static final String CURRENT_PATH_KEY = "currentPath";

    public PathTrackingFilter(Set<String> pathsToSkip) {
        this.pathsToSkip = pathsToSkip;
    }

    @Override
    public void serializeAsProperty(Object pojo, JsonGenerator gen, SerializationContext ctxt, PropertyWriter writer) {
        try {
            String currentPath = getCurrentPath(ctxt);
            String fullPath = buildFullPath(currentPath, writer.getName());

            if (pathsToSkip.contains(fullPath)) {
                return;
            }

            if (isComplexType(writer)) {
                try {
                    setCurrentPath(ctxt, fullPath);
                    writer.serializeAsProperty(pojo, gen, ctxt);
                } finally {
                    setCurrentPath(ctxt, currentPath);
                }
            } else {
                writer.serializeAsProperty(pojo, gen, ctxt);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(getLangValue("rest.tracking.path.error").formatted(ex.getMessage()));
        }
    }

    @Override
    public void serializeAsElement(Object elementValue, JsonGenerator gen, SerializationContext ctxt,
                                   PropertyWriter writer) throws Exception {
        writer.serializeAsElement(elementValue, gen, ctxt);
    }

    private String getCurrentPath(SerializationContext ctxt) {
        Object path = ctxt.getAttribute(CURRENT_PATH_KEY);

        return Objects.nonNull(path) ? path.toString() : "";
    }

    private void setCurrentPath(SerializationContext ctxt, String path) {
        ctxt.setAttribute(CURRENT_PATH_KEY, path.isEmpty() ? null : path);
    }

    private String buildFullPath(String currentPath, String fieldName) {
        if (currentPath.isEmpty()) {
            return fieldName;
        }

        return "%s.%s".formatted(currentPath, fieldName);
    }

    private boolean isComplexType(PropertyWriter writer) {
        Class<?> type = writer.getType().getRawClass();

        return type != null
                && !type.isPrimitive()
                && type != String.class
                && !Number.class.isAssignableFrom(type)
                && Boolean.class != type
                && Character.class != type;
    }
}
