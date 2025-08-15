package ru.origami.hibernate.attachment;

import ru.origami.hibernate.models.QueryParameter;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.origami.common.OrigamiHelper.getObjectAsJsonStringWithoutPrettify;
import static ru.origami.hibernate.attachment.HibernateLoggerFile.LOG_FILES_PATH;

public class QueryFormatter {

    public static String getSqlQuery(String inputQuery, List<QueryParameter> parameters) {
        File file = new File(String.format("%s/%s/hibernate-query-%s.txt", System.getProperty("user.dir"),
                LOG_FILES_PATH, Thread.currentThread().getName()));
        boolean isStandardEntity = true;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String queryWord = inputQuery != null ? inputQuery.trim().substring(0, inputQuery.trim().indexOf(" ")) : "";
            String queryField = "";

            if (inputQuery != null && inputQuery.trim().toLowerCase().startsWith("select")) {
                Pattern wordsCountPattern = Pattern.compile("^(\\w+) ([\\w\\., ]+) from.*");
                Matcher wordsCountMatcher = wordsCountPattern.matcher(inputQuery.toLowerCase());

                if (wordsCountMatcher.find()) {
                    isStandardEntity = !(wordsCountMatcher.group(2).contains(".") || wordsCountMatcher.group(2).contains(","));
                }

                Pattern query = Pattern.compile("^(\\w+)(\s|\n)((\\w+)(\\.\\w+)?)(\s|\n).*");
                Matcher queryMatcher = query.matcher(inputQuery);

                if (queryMatcher.find()) {
                    queryWord = queryMatcher.group(1).toLowerCase();

                    if (queryMatcher.group(5) != null) {
                        queryField = String.format("%s\\w+(\\.\\w+) AS \\w+_\\d+_\\d+_ from \\w+", queryMatcher.group(2));
                    }
                }
            }

            String startPattern = String.format("(\\d{2}:\\d{2}:\\d{2}.\\d{3} \\[[\\w-\\d]+] info - \\d+\\. )(%s%s.*)",
                    queryWord, queryField);
            Pattern fullQueryPattern = Pattern.compile(String.format("%s(?=\\{executed in)", startPattern), Pattern.CASE_INSENSITIVE);
            Pattern startQueryPattern = Pattern.compile(startPattern, Pattern.CASE_INSENSITIVE);

            String line;
            StringBuilder startQuery = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                Matcher fullMatcher = fullQueryPattern.matcher(line);
                Matcher startMatcher = startQueryPattern.matcher(line);

                if (fullMatcher.find()) {
                    return formatQuery(fullMatcher.group(2), queryField.isEmpty() ? null : fullMatcher.group(3), isStandardEntity, parameters);
                } else if (startMatcher.find()) {
                    startQuery.append(startMatcher.group(0));
                    break;
                }
            }

            while ((line = reader.readLine()) != null) {
                startQuery.append(line);
                Matcher fullMatcher = fullQueryPattern.matcher(startQuery.toString());

                if (fullMatcher.find()) {
                    return formatQuery(fullMatcher.group(2), queryField.isEmpty() ? null : fullMatcher.group(3), isStandardEntity, parameters);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            return inputQuery;
        } finally {
            try {
                new FileWriter(file).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return inputQuery;
    }

    protected static String formatQuery(String query, String fieldName, boolean isStandardEntity, List<QueryParameter> parameters) {
//        return new BasicFormatterImpl().format(formatQueryVision(formatQueryOperators(query), fieldName));
        String outputQuery = formatQueryVision(formatQueryOperators(query), fieldName, isStandardEntity);
        outputQuery = formatArray(outputQuery, parameters);

        return formatUuid(outputQuery);
    }

    private static String formatQueryOperators(String query) {
        ArrayList<String> templates = new ArrayList<>(Arrays.asList(
                "^(select |insert into |update |delete |alter table |alter trigger |create |declare(\\s|\n)|begin(\\s|\n))",
                "(\\((\\s)?select | values(\\s)?\\(| set | as | from | join | left | right | inner | outer | on | cross )",
                "( avg\\(| sum\\(| min\\(| max\\(| count\\(| distinct |cast\\()",
                "( where | and | or | case | case\\( | when | is | not | null | null\\)| end(\\))? | in | between | like )",
                "( \\(case | then | else )",
                "((\\s||>|<)now\\(\\)|today\\(\\)|tostring\\(|date_sub\\(|coalesce\\(|fetchval\\()|localtimestamp|current_timestamp",
                "( interval | month'| day'| minutes'|\\(minute,| \\(exists )",
                "( order by | desc | asc | group by | having | limit | random\\()| fetch first \\d+ rows only"));

        for (String template : templates) {
            Matcher mtch = Pattern.compile(template).matcher(query.toLowerCase());

            while (mtch.reset(query).find()) {
                query = mtch.replaceFirst(mtch.group().toUpperCase());
            }
        }

        return query;
    }

    private static int getCount(Map<String, String> tablesNames, Map.Entry<String, String> keyValue) {
        List<String> values = new ArrayList<>(tablesNames.values());
        List<String> modifiedValues = values.stream().map(el -> el.split("\\d")[0]).collect(Collectors.toList());

        return Collections.frequency(modifiedValues, keyValue.getValue());
    }

    @Deprecated
    private static String updatedFormatQueryVisionOld(String query, String fieldName) {
        Map<String, String> tablesNames = new HashMap<>();
        ArrayList<String> templates = new ArrayList<>(Arrays.asList(
                "(\\S\\B[^\\W\\d_]+0_)",
                "(SELECT([^)]+)AS([^)]+)\\n\\(CASE)",
                "(SELECT(?!.*(DISTINCT|TOP))(?!.*a-zA-Z)([^(]+)AS([^(]+)\\nFROM)",
                "(SELECT(?!.*(DISTINCT|TOP))([^(]+)AS([^)]+)\\n(\\t)?FROM)",
                "(\\S\\B[^\\W\\d_]+\\d_(?=\\W))"));
        String mssqlTopTemplate = "^((SELECT )(TOP (\\d+) ))";
        String mssqlTopValue = null;
        Matcher mtchMsSqlTop = Pattern.compile(mssqlTopTemplate).matcher(query);

        if (mtchMsSqlTop.find()) {
            mssqlTopValue = mtchMsSqlTop.group(4);
            query = mtchMsSqlTop.replaceFirst(String.format("%s", mtchMsSqlTop.group(2)));
        }

        for (String template : templates) {
            Matcher mtch = Pattern.compile(template).matcher(query);

            while (mtch.reset(query).find()) {
                switch (template) {
                    case "(\\S\\B[^\\W\\d_]+0_)" -> {
                        tablesNames.put(mtch.group(), mtch.group().substring(0, 3));

                        for (Map.Entry<String, String> keyValue : tablesNames.entrySet()) {
                            if (!keyValue.getKey().equals(mtch.group())) {
                                tablesNames.put(mtch.group(), mtch.group().substring(0, 3));
                            }

                            query = mtch.replaceFirst(String.format("%s", mtch.group().substring(0, 3)));
                        }
                    }
                    case "(SELECT([^)]+)AS([^)]+)\\n\\(CASE)" ->
                            query = mtch.replaceFirst(String.format("SELECT %s.*, \n(CASE", mtch.group().substring(7, 10)));
                    case "(SELECT(?!.*(DISTINCT|TOP))(?!.*a-zA-Z)([^(]+)AS([^(]+)\\nFROM)" ->
                            query = mtch.replaceFirst(String.format("SELECT %s%s \nFROM",
                                    mtch.group().substring(7, 10), fieldName == null ? ".*" : fieldName));
                    case "(SELECT(?!.*(DISTINCT|TOP))([^(]+)AS([^)]+)\\n(\\t)?FROM)" ->
                            query = mtch.replaceFirst(String.format("SELECT %s.* \nFROM", mtch.group().substring(7, 10)));
                    default -> {
                        tablesNames.put(mtch.group(), mtch.group().substring(0, 3));
                        query = updateDefaultFormatQueryVision(tablesNames, mtch, query);
                    }
                }
            }
        }

        query = query.replaceAll("( AS \\w+_\\d+_\\d+_)", "");

        if (mssqlTopValue != null) {
            query = query.replaceFirst("SELECT ", String.format("SELECT TOP %s ", mssqlTopValue));
        }

        return query;
    }

    private static String updatedFormatQueryVision(String query, String fieldName, boolean isStandardEntity) {
        Map<String, String> tablesNames = new HashMap<>();
        Matcher fromMatcher = Pattern.compile("(\n|\s)FROM ").matcher(query); // ищем строку до и после первой таблицы
        int fromIndex = fromMatcher.find() ? fromMatcher.start() : 0;

        if (fromIndex == 0) {
            return query;
        }

        String selectStr = query.substring(0, fromIndex); // строка ДО
        String strAfterSelect = query.substring(fromIndex); // строка ПОСЛЕ
        Matcher mtch = Pattern.compile("((\n|\s)(FROM|JOIN) ([\"\\w_]+)\\.([\\w_]+)\s([\\w_]+))").matcher(strAfterSelect);

        while (mtch.find()) {
            int index = 1;
            String name = mtch.group(5).replaceAll("[-_]", "").substring(0, 3); // получаем имя таблицы без -

            if (tablesNames.containsValue(name)) {
                while (tablesNames.containsValue(String.format("%s%d", name, index))) { // если сокращение есть, то добавляем цифру
                    index++;
                }

                name = String.format("%s%d", name, index);
            }

            tablesNames.put(mtch.group(6), name);
        }

        for (Map.Entry<String, String> keyValue : tablesNames.entrySet()) { // заменяем во всех строках сокращения
            selectStr = selectStr.replaceAll(keyValue.getKey(), keyValue.getValue());
            strAfterSelect = strAfterSelect.replaceAll(keyValue.getKey(), keyValue.getValue());
        }

        String mssqlTopTemplate = "^((SELECT )(TOP (\\d+) ))";
        String mssqlTopValue = null;
        Matcher mtchMsSqlTop = Pattern.compile(mssqlTopTemplate).matcher(selectStr);

        if (mtchMsSqlTop.find()) {
            mssqlTopValue = mtchMsSqlTop.group(4);
            selectStr = mtchMsSqlTop.replaceFirst(String.format("%s", mtchMsSqlTop.group(2)));
        }

        String selectTemplate = "(.*)(CASE.*END(AS \\w+))"; // ищем в первом селекте все вхождения CASE
        List<String> caseValues = new ArrayList<>();
        Matcher caseMather = Pattern.compile(selectTemplate).matcher(selectStr);

        while (caseMather.reset(selectStr).find()) {
            caseValues.add(caseMather.group(2));
        }

        // если таблица в запросе одна, то сокращаем селект до *
        // иначе скорее всего этот селект собирается в кастомную сущность и в нем нельзя заменить на *
        if (isStandardEntity) {
            Pattern pattern = Pattern.compile("^(\\w+) (.*)\nfrom (.*) (\\w+).*");
            Matcher matcher = pattern.matcher(query.toLowerCase());

            if (matcher.find()) { // TODO обработать ситуацию, когда у нас (select *)
                selectStr = String.format("%s.*", selectStr.substring(0, selectStr.indexOf(".")));
            }
        }

        for (String caseValue : caseValues) {
            selectStr = String.format("%s, %s", selectStr, caseValue);
        }

        if (mssqlTopValue != null) {
            selectStr = selectStr.replaceFirst("SELECT ", String.format("SELECT TOP %s ", mssqlTopValue));
        }

        return selectStr + strAfterSelect;
    }

    private static String updateDefaultFormatQueryVision(Map<String, String> tablesNames, Matcher mtch, String query) {
        for (Map.Entry<String, String> keyValue : tablesNames.entrySet()) {
            if (!keyValue.getKey().equals(mtch.group()) && keyValue.getValue().equals(mtch.group().substring(0, 3))) {
                if (keyValue.getValue().equals(mtch.group().substring(0, 3))
                        && !keyValue.getKey().split("\\d")[0].equals(mtch.group().split("\\d")[0])) {
                    tablesNames.put(mtch.group(), String.format("%s%d", mtch.group().substring(0, 4), getCount(tablesNames, keyValue)));
                    query = mtch.replaceFirst(String.format("%s%d", mtch.group().substring(0, 4), getCount(tablesNames, keyValue)));
                } else {
                    tablesNames.put(mtch.group(), String.format("%s%d", mtch.group().substring(0, 3), getCount(tablesNames, keyValue) - 1));
                    query = mtch.replaceFirst(String.format("%s%d", mtch.group().substring(0, 3), getCount(tablesNames, keyValue) - 1));
                }
            } else if (keyValue.getKey().equals(mtch.group())) {
                query = mtch.replaceFirst(String.format("%s", keyValue.getValue()));
            }
        }

        return query;
    }

    private static String formatQueryVision(String query, String fieldName, boolean isStandardEntity) {
        ArrayList<String> templates = new ArrayList<>(Arrays.asList(
                "(( (LEFT|RIGHT|INNER|FULL|CROSS)( OUTER)?)? JOIN)",
                "( FROM | WHERE | AND | ORDER BY | GROUP BY | HAVING | LIMIT | VALUES(\\s)?\\()",
                "( \\(CASE| SET | FETCH FIRST (\\d)+ ROWS ONLY)",
                "((=| )\\(SELECT([^)]+)\\) |(=| )\\(( )?SELECT[^\"]*\\))",
                "(\\([^(]+BETWEEN([^)]+)\\nAND([^)]+)\\))",
                "( DO | DECLARE | BEGIN | END;| $$;| INSERT INTO | VALUES | UPDATE | DELETE )",
                "( SELECT | ALTER TABLE | ALTER TRIGGER | CREATE )"));

        for (String template : templates) {
            Matcher mtch = Pattern.compile(template).matcher(query);

            while (mtch.reset(query).find()) {
                if (!template.equals("((=| )\\(SELECT([^)]+)\\) |(=| )\\(( )?SELECT[^\"]*\\))") && !template.equals("(\\([^(]+BETWEEN([^)]+)\\nAND([^)]+)\\))")) {
                    query = mtch.replaceFirst(String.format(" \n%s", mtch.group().stripLeading()));
                } else if (template.equals("((=| )\\(SELECT([^)]+)\\) |(=| )\\(( )?SELECT[^\"]*\\))")) {
                    String format = "\n\t%s";

                    if (mtch.group().startsWith("=")) {
                        format = "=\n\t%s";
                    }

                    query = mtch.replaceFirst(String.format(format, mtch.group().replaceFirst("^=", "")
                            .stripLeading().replace("\n", "\n\t")));
                } else {
                    query = mtch.replaceFirst(String.format("%s", mtch.group().replace("\n", "")));
                }
            }
        }

        query = query.replaceAll("(LEFT|RIGHT|INNER|FULL|CROSS)( OUTER)? \nJOIN", "$1$2 JOIN");

        return updatedFormatQueryVision(query, fieldName, isStandardEntity);
    }

    private static String formatArray(String query, List<QueryParameter> parameters) {
        if (Objects.nonNull(parameters)) {
            parameters = parameters.stream()
                    .filter(p -> Objects.nonNull(p.getBindableType()))
                    .filter(p -> p.getValue() instanceof Object[])
                    .collect(Collectors.toList());

            if (parameters.size() == 1) {
                query = query.replaceAll("'<Array>'", getMassiveStringValuesForQuery(parameters.get(0)));
            } else {
                for (QueryParameter param : parameters) {
                    query = query.replaceFirst("'<Array>'", getMassiveStringValuesForQuery(param));
                }
            }
        }

        return query;
    }

    private static String getMassiveStringValuesForQuery(QueryParameter parameter) {
        String query = "ARRAY%s".formatted(getObjectAsJsonStringWithoutPrettify(parameter.getValue()));

        for (Object value : (Object[]) parameter.getValue()) {
            if (value instanceof Object[] subMassive) {
                for (Object subValue : subMassive) {
                    query = query.replaceAll("\"%s\"".formatted(subValue), "'%s'".formatted(subValue));
                }
            } else {
                query = query.replaceAll("\"%s\"".formatted(value), "'%s'".formatted(value));
            }
        }

        return query;
    }

    private static String formatUuid(String query) {
        //31e3d5ca-ff88-47e4-82a3-f4db04e136a1
        return query.replaceAll("(\\(|\s)([A-Za-z0-9]{8}\\-[A-Za-z0-9]{4}\\-[A-Za-z0-9]{4}\\-[A-Za-z0-9]{4}\\-[A-Za-z0-9]{12})(,|\\))", "$1'$2'$3")
                .replaceAll("(=( )?)([A-Za-z0-9]{8}\\-[A-Za-z0-9]{4}\\-[A-Za-z0-9]{4}\\-[A-Za-z0-9]{4}\\-[A-Za-z0-9]{12})(\s|\n|)", "$1'$3'$4");
    }
}