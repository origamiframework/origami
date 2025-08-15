package ru.origami.hibernate.utils;

import org.hibernate.resource.jdbc.internal.EmptyStatementInspector;

import java.util.Objects;

import static ru.origami.hibernate.CommonFixtureSteps.DYNAMIC_SCHEMA;

public class SchemaInspector extends EmptyStatementInspector {

    private String schema;

    public SchemaInspector(String schema) {
        this.schema = schema;
    }

    @Override
    public String inspect(String sql) {
        String prepedStatement = super.inspect(sql);
        prepedStatement = changeSchemaIfPresent(prepedStatement);

        return prepedStatement;
    }

    private String changeSchemaIfPresent(String query) {
        if (Objects.nonNull(schema)) {
            return query.replaceAll(DYNAMIC_SCHEMA.replaceAll("\\{", "\\\\{")
                    .replaceAll("\\$", "\\\\\\$"), schema);
        }

        return query;
    }
}
