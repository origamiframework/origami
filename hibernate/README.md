# Origami Hibernate

* [Описание](#описание)
* [Подключение](#подключение)
* [Использование](#использование)
* [Динамическая схема](#динамическая-схема)
* [Схема по умолчанию](#схема-по-умолчанию)
* [Параметры](#параметры)
* [Основной Readme](../README.md)


## Описание

Библиотека для работы с базами данных на основе [Hibernate](https://hibernate.org/orm/documentation/6.1/).

## Подключение

Необходимо добавить зависимость в pom.xml в проекте:
```XML
    <dependency>
        <groupId>ru.origamiframework</groupId>
        <artifactId>origami-framework-hibernate</artifactId>
    </dependency>
```

Так же необходимо создать файл конфигурации для работы с требуемой базой:
* hibernate-postgres.cfg.xml
* hibernate-clickhouse.cfg.xml
* hibernate-oracle.cfg.xml
* hibernate-mssql.cfg.xml

В файле необходимо указать параметр со значением класса драйвера:

    <property name="connection.driver_class">ru.origami.hibernate.log4jdbc.sql.jdbcapi.DriverSpy</property>

## Использование

Необходимо унаследовать класс с шагами для работы с конкретной базой данных от [CommonFixtureSteps](src/main/java/ru/origami/hibernate/CommonFixtureSteps.java).
<br/>В конструкторе класса в <b>sessionProperties</b> необходимо указать параметры для подключения и после проинициализировать сессию.
<br/>Пример:
```JAVA
public class DatabaseFixtureSteps extends CommonFixtureSteps {

    public DatabaseFixtureSteps() {
        sessionProperties = new DataBaseSessionProperties.Builder()
                .setHibernateResource(EHibernateResource.POSTGRES)
                .setDbHost("host")
                .setDbPort("port")
                .setDbName("db.name")
                .setDbUserName("db.username")
                .setDbPassword("db.password")
                .build();

        initSession();
    }
}
```

Запросы SELECT рекомендуется выполнять на языке hql.
```JAVA
@Step("Получаем ... из ...")
public Table getRandomCustId() {
    final String hql = """
        SELECT t FROM Table t
        ORDER BY id
        """;

    return session.createQuery(hql, Table.class)
        .setMaxResults(1)
        .getSingleResult();
}
```

Запросы INSERT, UPDATE, DELETE и тд. рекомендуется выполнять на языке sql.
```JAVA
@Step("Обновляем записи в таблице table по id: {0}")
public void updateDayByDecisionId(Long id) {
    final String sql = """
        UPDATE table
        SET day = 19
        WHERE id = :id
        """;

    session.createNativeQuery(sql)
        .setParameter("id", id)
        .executeUpdate();
}
```

## Динамическая схема

При необходимости использования различных схем, например, в рамках различных стендов, реализована возможность использования динамических схем.
Для этого в схеме таблицы необходимо указать схему <b>DYNAMIC_SCHEMA</b>.
```JAVA
    @Table(name = "fixed_accepted_rate", schema = DYNAMIC_SCHEMA)
```

В параметрах подключения в таком случае необходимо указать схему
```JAVA
public class DatabaseFixtureSteps extends CommonFixtureSteps {

    public DatabaseFixtureSteps() {
        sessionProperties = new DataBaseSessionProperties.Builder()
                .....
                .setSchema("db.schema")
                .build();

        initSession();
    }
}
```

Таким образом запрос будет выполнен с той или иной схемой в зависимости от переданного параметра.

## Схема по умолчанию

При выполнении некоторых запросов, в БД может срабатывать триггер, например, при выполнении insert,
и будет вызываться функция. Выполнение такой функции может проходить в той схеме, к которой установлено подключение,
т.е. без явной привязки к схеме.
Для такой ситуации добавлена возможно установки подключения к конкретной схеме БД.

В параметрах подключения необходимо указать схему по умолчанию
```JAVA
public class DatabaseFixtureSteps extends CommonFixtureSteps {

    public DatabaseFixtureSteps() {
        sessionProperties = new DataBaseSessionProperties.Builder()
                .....
                .setDefaultSchema("db.default.schema")
                .build();

        initSession();
    }
}
```

## Параметры

<b>hibernate.excel.result.enabled</b> - параметр необходим для прикрепления результата выборки из базы данных в формате Excel.
По умолчанию: false.