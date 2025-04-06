
# Сборка проекта

## Сборка проекта с тестами

- Для выполнения тестов в ходе сборки Maven-проекта требуется наличие БД PostgreSQL с необходимой структурой в виде тестовых таблиц.

  Для этого необходимо:

  1. в имеющейся БД PostgreSQL применить скрипт [test_postgres.schema.sql](..%2Ffiles%2Fresources%2Fsql%2Ftest_postgres.schema.sql) который создаст тестовое окружение
  2. в файле [test.properties](..%2Ftest.properties) указать параметры подключения к этой тестовой БД

- Собрать проект с помощью Maven выполнив в директории проекта команду:

```bash
./mvnw clean install
```

## Сборка проекта без тестов

В директории проекта выполните следующую команду с использованием Maven:

```bash
./mvnw -Dmaven.test.skip=true clean install
```

## Сборка и запуск проекта без Docker Compose
Данный вариант сборки и запуска проекта рассчитан на разработчиков которые хотят получить полный контроль над используемым сервисом для углубленного знакомства с ним.

### Необходимые компоненты

- **Git**
- **Open JDK 17 и выше**

### Порядок действий
#### Скачать репозиторий проекта:
```bash
git clone https://gitverse.ru/sbertech/dataspace-ce.git
```

#### Собрать проект

  Сборка проекта возможна в двух вариантах: с тестированием и без тестирования.

  Для упрощения процесса знакомства, выполним сборку без тестирования. Для этого в директории проекта выполните следующую команду:

```bash
./mvnw -Dmaven.test.skip=true clean install
```
Для сборки проекта с тестированием обратитесь к разделу "Сборка проекта с тестами"


#### Настройка подключения к внешней БД

Для настройки подключения к внешней БД внесите изменения в файл [context-child.properties](../files%2Fresources%2Fsrc-model%2Fcontext-child.properties).

Как правило, в качестве БД используется развернутый экземпляр на удаленном сервере. Параметры для подключения к нему уточните у своего администратора.

Также возможен упрощенный локальный запуск БД PostgreSQL с помощью Docker.
Для этого измените в [context-child.properties](../files%2Fresources%2Fsrc-model%2Fcontext-child.properties) эти строки следующим образом:
```json
spring.datasource.url=jdbc:postgresql://localhost:5432/dspcdb?currentSchema=dspc
spring.datasource.username=dspc
spring.datasource.password=dspcpwd
...
liquibase.defaultSchema=public
liquibase.parameters.defaultSchemaName=public
```
Запустите Docker-контейнер с БД PostgreSQL:

```bash
docker run --name dspc-pg-16.6 -p 5432:5432 -e POSTGRES_USER=dspc -e POSTGRES_PASSWORD=dspcpwd -e POSTGRES_DB=dspcdb -d postgres:16.6
```
Убедитесь, что контейнер запустился (поле STATUS=Up):
```bash
docker ps
```

#### Сборка модели
Для подготовки модели данных выполните команду:
```bash
java -jar modules/model-release/target/model-release-DEV-SNAPSHOT-runnable.jar model-directory=files/resources/src-model run-liquibase=true target-directory=files/resources/build-model
```

В результате выполнения данной команды:<br>
* в каталоге [files/resources/build-model](files%2Fresources%2Fbuild-model) будут созданы файлы <strong>context-child.properties</strong> и <strong>pdm.xml</strong>, а также опциональные файлы безопасности: <strong>graphql-permissions.json</strong>, <strong>jwks.json</strong>, при наличии их в исходной директории
* в базе данных будут созданы таблицы в соответствии с моделью данных


***Важно*** При каждом запуске подготовки модели, в каталоге files/resources/src-model/model будет сформировано релизное состояние этой модели.
При повторном запуске без изменения версии модели  будет сформировано сообщение:

```
--------- Reason: When analyzing changeLog, an earlier version of the model was found to be used 0.0.1
--------- Solution: Install a different version of the model
```
В процессе знакомства с проектом по разным причинам возможны множественные неудачные запуски, что может привести к появлению данного сообщения.
Удалите каталог files/resources/src-model/model и продолжите настройку.

Более подробно об изменении модели читайте в документации [Изменение модели данных](./model-guide.md)

#### Запуск приложения

Для запуска приложения выполните команду:
```bash
java -jar modules/dataspace-app/target/dataspace-app-DEV-SNAPSHOT.jar --dataspace.app.pathConfigDirectory=files/resources/build-model/ --dataspace.app.singleMode=true --dataspace.app.pdmZipped=false
```

После успешного запуска список загруженных моделей в виде JSON-документа можно увидеть по адресу: http://localhost:8080/actuator/models

При этом раздел <strong>endpoints</strong> содержит адреса, по которым можно отправлять GraphQL-запросы от клиентов (graphql), а также адрес web-интерфейса для работы с GraphQL (graphiql).
```json
        "graphql" : "/models/1/graphql",
        "graphiql" : "/graphiql?path=/models/1/graphql"
```
Также приложение позволяет выполнять GraphQL-запросы в удобном web-редакторе:
http://localhost:8080/graphiql?path=/models/1/graphql

## Полная пересборка и запуск проекта при использовании Docker Compose
При использовании скрипта [build_and_start.sh](../build_and_start.sh) для работы с проектом используется механизм исключения повторной
сборки артефактов при втором и последующих запусках. Данный подход значительно сокращает время запуска.

Если всеже необходимо вновь выполнить полную сборку артефактов, удалите из корня проекта файл first_build_complete.lock


## Выключение проекта в Docker Compose с удалением данных в БД

При работе с проектом через скрипт запуска [build_and_start.sh](../build_and_start.sh) контейнеры сервисов (БД, application, build&deploy
model) управляются через Docker Compose.

Важно учитывать, что стандартная команда остановки Docker Compose сохраняет данные, связанные с томами (volumes), что приводит к сохранению
состояния БД между перезапусками.

Если необходимо полностью вновь пересоздать структуру в БД выключите запущенный проект следующим образом:

```bash
cd ./docker; docker-compose down -v; cd ..
```

## Выключение проекта в Docker Compose без удаления данных в БД

Выполните команды:
```bash
cd ./docker; docker-compose down; cd ..
```


# Подключение к БД в Docker Compose

В случае запуска сервиса через скрипт [build_and_start.sh](../build_and_start.sh), возможно прямое подключение к используемой БД PostgreSQL.

Параметры подключения к БД отображаются в конце вывода работы скрипта [build_and_start.sh](../build_and_start.sh)

Пример:

```text
...
Application is running!
Database connection parameters:
URL: jdbc:postgresql://localhost:5432/dspcdb?currentSchema=public
Username: dspc
Password: 008585
Useful links:
- Models: http://localhost:8080/actuator/models
- GraphiQL: http://localhost:8080/graphiql?path=/models/1/graphql

```

На основе строк с префиксами URL, Username, Password получаем необходимые параметры:

- IP = localhost
- Port = 5432
- DbName = dspcdb
- Schema = public
- Username = dspc
- Password = 008585


**Важно:** пароль генерируется случайным образом

Далее эти параметры необходимо использовать в приложении ([DBeaver](https://dbeaver.io/download/), [pgAdmin](https://www.pgadmin.org/download/), psql и тп) для подключения к БД PostgreSQL.
