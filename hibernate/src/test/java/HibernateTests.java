import io.hypersistence.utils.hibernate.type.array.IntArrayType;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import org.junit.jupiter.api.Test;
import ru.origami.hibernate.attachment.QueryFormatter;
import ru.origami.hibernate.models.QueryParameter;

import java.util.List;

import static java.util.Collections.singletonList;
import static ru.origami.common.asserts.Asserts.assertEquals;

public class HibernateTests extends QueryFormatter {

    @Test
    public void successParameterAsArray() {
        String inputQuery = """
                INSERT INTO service.event (id, created_at, updated_at, version, code, system_name, description, parameters, updated_by) VALUES(31e3d5ca-ff88-47e4-82a3-f4db04e136a1, '03/04/2025 07:55:49.525', '03/04/2025 07:55:49.525', 0, 'dafsdfasdfаasdfasdfфвыавыаdsa', 'idp', 'Создание ИИИ', '<Array>', 'ttt')
                """;
        String expectedQuery = """
                INSERT INTO service.event (id, created_at, updated_at, version, code, system_name, description, parameters, updated_by)\s
                VALUES('31e3d5ca-ff88-47e4-82a3-f4db04e136a1', '03/04/2025 07:55:49.525', '03/04/2025 07:55:49.525', 0, 'dafsdfasdfаasdfasdfфвыавыаdsa', 'idp', 'Создание ИИИ', ARRAY['qwe','rty'], 'ttt')
                """;

        List<QueryParameter> parameterCollectionList = singletonList(QueryParameter.Builder()
                .setValue(new String[]{"qwe", "rty"})
                .setBindableType(new StringArrayType())
                .build());

        assertEquals("query", expectedQuery, formatQuery(inputQuery, null, true, parameterCollectionList));
    }

    @Test
    public void successParameterAsIntArray() {
        String inputQuery = """
                INSERT INTO service.event (id, created_at, updated_at, version, code, system_name, description, parameters, updated_by) VALUES(31e3d5ca-ff88-47e4-82a3-f4db04e136a1, '03/04/2025 07:55:49.525', '03/04/2025 07:55:49.525', 0, 'dafsdfasdfаasdfasdfфвыавыаdsa', 'idp', 'Создание ИИИ', '<Array>', 'ttt')
                """;
        String expectedQuery = """
                INSERT INTO service.event (id, created_at, updated_at, version, code, system_name, description, parameters, updated_by)\s
                VALUES('31e3d5ca-ff88-47e4-82a3-f4db04e136a1', '03/04/2025 07:55:49.525', '03/04/2025 07:55:49.525', 0, 'dafsdfasdfаasdfasdfфвыавыаdsa', 'idp', 'Создание ИИИ', ARRAY[123,456], 'ttt')
                """;

        List<QueryParameter> parameterCollectionList = singletonList(QueryParameter.Builder()
                .setValue(new Integer[]{123, 456})
                .setBindableType(new IntArrayType())
                .build());

        assertEquals("query", expectedQuery, formatQuery(inputQuery, null, true, parameterCollectionList));
    }

    @Test
    public void successParameterAsMultiArray() {
        String inputQuery = """
                INSERT INTO service.event (id, created_at, updated_at, version, code, system_name, description, parameters, updated_by) VALUES(31e3d5ca-ff88-47e4-82a3-f4db04e136a1, '03/04/2025 07:55:49.525', '03/04/2025 07:55:49.525', 0, 'dafsdfasdfаasdfasdfфвыавыаdsa', 'idp', 'Создание ИИИ', '<Array>', 'ttt')
                """;
        String expectedQuery = """
                INSERT INTO service.event (id, created_at, updated_at, version, code, system_name, description, parameters, updated_by)\s
                VALUES('31e3d5ca-ff88-47e4-82a3-f4db04e136a1', '03/04/2025 07:55:49.525', '03/04/2025 07:55:49.525', 0, 'dafsdfasdfаasdfasdfфвыавыаdsa', 'idp', 'Создание ИИИ', ARRAY[['qwe','rty'],['asd','fgh']], 'ttt')
                """;

        List<QueryParameter> parameterCollectionList = singletonList(QueryParameter.Builder()
                .setValue(new String[][]{{"qwe", "rty"},{"asd", "fgh"}})
                .setBindableType(new StringArrayType())
                .build());

        assertEquals("query", expectedQuery, formatQuery(inputQuery, null, true, parameterCollectionList));
    }

    @Test
    public void successUuid() {
        String inputQuery = """
                INSERT INTO clients(id, client_id, group_id) VALUES (0F5B00B4-1DDD-4096-870C-737D7F548F43, E88DBD76-6285-4CEC-BED3-E67549ABB111, E88DBD76-6285-4CEC-BED3-E67549ABBB01)
                """;
        String expectedQuery = """
                INSERT INTO clients(id, client_id, group_id)\s
                VALUES ('0F5B00B4-1DDD-4096-870C-737D7F548F43', 'E88DBD76-6285-4CEC-BED3-E67549ABB111', 'E88DBD76-6285-4CEC-BED3-E67549ABBB01')
                """;

        assertEquals("query", expectedQuery, formatQuery(inputQuery, null, true, null));
    }

    @Test
    public void successUuid2() {
        String inputQuery = """
                INSERT INTO clients(id, client_id, group_id) VALUES ('0F5B00B4-1DDD-4096-870C-737D7F548F43', 'E88DBD76-6285-4CEC-BED3-E67549ABB111', 'E88DBD76-6285-4CEC-BED3-E67549ABBB01')
                """;
        String expectedQuery = """
                INSERT INTO clients(id, client_id, group_id)\s
                VALUES ('0F5B00B4-1DDD-4096-870C-737D7F548F43', 'E88DBD76-6285-4CEC-BED3-E67549ABB111', 'E88DBD76-6285-4CEC-BED3-E67549ABBB01')
                """;

        assertEquals("query", expectedQuery, formatQuery(inputQuery, null, true, null));
    }

    @Test
    public void successJoin() {
        String inputQuery = """
                select g1_0.id,c1_0.group_id,c1_1.id,c1_1.is_removable,c1_1.name,c1_1.note,c1_1.type_id,g1_0.is_online_changes,g1_0.is_order_changes,g1_0.name,o1_0.group_id,o1_0.id,o1_0.operation_type_id,o1_0.request_type,o1_0.version,g1_0.order_type,r1_0.group_id,r1_1.id,r1_1.name,r1_1.precise,r1_1.symbol,r1_1.value_type,r1_1.version from "public".groups g1_0 left join "public".clien_gr c1_0 on g1_0.id=c1_0.group_id left join "public".clients c1_1 on c1_1.id=c1_0.client_id left join "public".grou_op o1_0 on g1_0.id=o1_0.group_id left join "public".rule_gr r1_0 on g1_0.id=r1_0.group_id left join "public".trad_ru r1_1 on r1_1.id=r1_0.rule_id where g1_0.id=E88DBD76-6285-4CEC-BED3-E67549ABBB01 AND cli1.id=E88DBD76-6285-4CEC-BED3-E67549ABBB01
                """;
        String expectedQuery = """
                SELECT gro.*
                FROM "public".groups gro\s
                LEFT JOIN "public".clien_gr cli ON gro.id=cli.group_id\s
                LEFT JOIN "public".clients cli1 ON cli1.id=cli.client_id\s
                LEFT JOIN "public".grou_op gro1 ON gro.id=gro1.group_id\s
                LEFT JOIN "public".rule_gr rul ON gro.id=rul.group_id\s
                LEFT JOIN "public".trad_ru tra ON tra.id=rul.rule_id\s
                WHERE gro.id='E88DBD76-6285-4CEC-BED3-E67549ABBB01'\s
                AND cli1.id='E88DBD76-6285-4CEC-BED3-E67549ABBB01'
                """;

        assertEquals("query", expectedQuery, formatQuery(inputQuery, null, true, null));
    }
}
