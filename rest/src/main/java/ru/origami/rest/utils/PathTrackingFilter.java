package ru.origami.rest.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

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
    public void serializeAsField(Object pojo, JsonGenerator gen, SerializerProvider prov,
                                 PropertyWriter writer) {
        try {
            String currentPath = getCurrentPath(prov);
            String fullPath = buildFullPath(currentPath, writer.getName());

            if (pathsToSkip.contains(fullPath)) {
                return;
            }

            if (isComplexType(writer)) {
                try {
                    setCurrentPath(prov, fullPath);
                    writer.serializeAsField(pojo, gen, prov);
                } finally {
                    setCurrentPath(prov, currentPath);
                }
            } else {
                writer.serializeAsField(pojo, gen, prov);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(getLangValue("rest.tracking.path.error").formatted(ex.getMessage()));
        }
    }

    @Override
    public void serializeAsElement(Object elementValue, JsonGenerator gen, SerializerProvider prov,
                                   PropertyWriter writer) throws Exception {
        writer.serializeAsElement(elementValue, gen, prov);
    }

    private String getCurrentPath(SerializerProvider prov) {
        Object path = prov.getAttribute(CURRENT_PATH_KEY);

        return Objects.nonNull(path) ? path.toString() : "";
    }

    private void setCurrentPath(SerializerProvider prov, String path) {
        prov.setAttribute(CURRENT_PATH_KEY, path.isEmpty() ? null : path);
    }

    private String buildFullPath(String currentPath, String fieldName) {
        if (currentPath.isEmpty()) {
            return fieldName;
        }

        return "%s.%s".formatted(currentPath, fieldName);
    }

    private boolean isComplexType(PropertyWriter writer) {
        Class<?> type = writer.getType().getRawClass();

        return (type != null && !type.isPrimitive() && type != String.class && !Number.class.isAssignableFrom(type)
                && Boolean.class != type && Character.class != type);
    }
}
