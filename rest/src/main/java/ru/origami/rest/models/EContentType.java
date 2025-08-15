package ru.origami.rest.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum EContentType {

    TEXT("text/plain", ".txt", new ObjectMapper(), false),
    JSON("application/json", ".json", new ObjectMapper(), false),
    XML("application/xml", ".xml", new XmlMapper(), false),
    EXCEL("application/vnd.ms-excel", ".xls", null, true),
    OPEN_EXCEL("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx", null, true),
    DOC("application/msword", ".doc", null, true),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx", null, true),
    PDF("application/pdf", ".pdf", null, true),
    ZIP("application/zip", ".zip", null, true),
    GZIP("application/gzip", ".gz", null, true),
    RAR("application/vnd.rar", ".rar", null, true),
    ZIP_7("application/x-7z-compressed", ".7z", null, true),
    CSV("text/csv", ".csv", null, true),
    JPEG("image/jpeg", ".jpeg", null, true);

    private String contentType;

    private String fileExtension;

    private ObjectMapper mapper;

    private boolean isByteArrayResponse;

    public static Set<EContentType> getContentTypesWithByteArrayResp() {
        return Arrays.stream(EContentType.values())
                .filter(EContentType::isByteArrayResponse)
                .collect(Collectors.toSet());
    }

    public static EContentType getContentTypeByType(String type) {
        return Arrays.stream(EContentType.values())
                .filter(ct -> ct.getContentType().equals(type))
                .findFirst()
                .orElse(null);
    }

    public static EContentType getContentTypeByExtension(String extension) {
        return Arrays.stream(EContentType.values())
                .filter(ct -> ct.getFileExtension().equals(String.format(".%s", extension)))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return String.format("type: '%s', extension: '%s'", contentType, fileExtension);
    }
}
