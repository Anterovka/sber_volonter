# Platform V DataSpace Community Edition
Platform V DataSpace Community Edition  – это программный компонент, который помогает ускорить создание бизнес-приложений и уменьшить затраты на их разработку.


## Ключевые возможности продукта
- Быстрое создание решения для работы с данными на основе модели предметной области
- Гибкий поиск и манипулирование данными (создание, изменение, удаление) с помощью языка запросов GraphQL

## Необходимые компоненты

- **Git**
- **Open JDK 17 и выше**
- **Docker и Docker Compose** версии 2.29 и выше<br>
При установке Docker важно также установить WSL

Официальные сайты:
- Git: https://git-scm.com/
- OpenJDK https://jdk.java.net/archive/
- Docker: https://www.docker.com/

## Установка и запуск

Далее будет рассмотрен процесс запуска DataSpace с моделью данных по умолчанию на примере медицинской клиники. Помимо это, также есть возможность ознакомиться с работой продукта на примере [fullstack-приложения "Трекер задач"](./examples/todolist/), в рамках которого можно увидеть взаимодействие DataSpace с frontend-приложением, а также системой авторизации и аутентификации (KeyCloak) для более комплексного понимания возможностей системы.

1. Скачайте репозиторий проекта:
```bash
git clone https://gitverse.ru/sbertech/dataspace-ce.git
```
2. Перейдите в папку скачанного проекта:
```bash
cd dataspace-ce
```
3. Запуск проекта:

Возможны 2 вариант запуска:
- быстрый с готовыми Docker образами (упрощенный режим)

```bash
./quick_start.sh
```
- сборка проекта, подготовка и запуск локальных Docker-образов

```bash
./build_and_start.sh
```
В процессе исполнения в терминале будет отображаться различная диагностическая информация о ходе процесса сборки и запуска приложения. Также в файле скрипта присутствуют краткие комментарии выполняемых действий на случай необходимости более детального понимания.
<br>

![](examples/todolist/docs/resources/31.png)

После успешного запуска DataSpace можно увидеть список загруженных моделей по адресу:<br>
http://localhost:8080/actuator/models

Также появляется возможность создавать и выполнять GraphQL-запросы к модели данных DatSpace в удобном Web-редакторе:<br>
http://localhost:8080/graphiql?path=/models/1/graphql


По умолчанию в приложении загружается модель с примером сервиса
["Медицинская клиника: запись на прием"](files/resources/src-model/model.xml)

При необходимости загрузки своей модели, файл с ее описанием необходимо разместить в каталоге
[files/resources/src-model](files/resources/src-model), имя файла - <strong>model.xml</strong>. Более подробную информацию о структуре и правилах описания модели можно найти в [Руководстве по ведению модели](./documentation/model-guide.md)



## Выполнение GraphQL-запросов на примере модели *"Медицинская клиника: запись на прием"*

Для быстрого ознакомления с GraphQL перейдите в Web-редактор http://localhost:8080/graphiql?path=/models/1/graphql

 ![](documentation/images/readme/graphql1.png)

В раздел Variables укажите значения атрибутов для запроса на создание сущности:

```json
{
  "input": {
    "name": "Городская поликлиника №1",
    "address": {
      "city": "Москва",
      "street": "ул. Ленина",
      "flatNo": "10"
    }
  }
}
```
Затем выполните GraphQL-запрос *createClinicMutation* через Web-редактор:

```graphql
# Создание клиники
mutation createClinicMutation($input: _CreateClinicInput!) {
  packet {
    createClinic(input: $input) {
      id
      name
      address {
        city
        street
        flatNo
      }
    }
  }
}
```


Для просмотра списка клиник выполните запрос *searchClinicsQuery*:

```graphql
# Вывод списка клиник
query searchClinicsQuery {
  searchClinic {
    elems {
      id
      name
      address {
        city
      }
    }
  }
}

```
![](documentation/images/readme/search.png)



По умолчанию в демо-примере отключен функционал разграничения прав доступа.
Функционал разграничения прав доступа (далее Security) способен ограничивать доступ к данным на основе разрешений (*permissions*).
Для его включения необходимо раскомментировать в файле [context-child.properties](files%2Fresources%2Fsrc-model%2Fcontext-child.properties)
параметр, определяющий правила описания логики разрешений (*permissions*):
```
dataspace.security.graphql.permissions.source=file
```

После этого необходимо:
- увеличить версию модели в файле [model.xml](./files/resources/src-model/model.xml)

  ````
  Например:
  <model model-name=.......   version="0.0.2" >
  ````

  Подробнее об изменении модели обратитесь к разделу [Изменение модели данных](./documentation/model-guide.md) в [model-guide.md](./documentation/model-guide.md)
- вновь выполнить:
```bash
./quick_start.sh  или ./build_and_start.sh
```

Для выполнения запросов в режиме Security через Web-редактор необходимо добавить токен JWT (JSON Web Token)в раздел Headers:
```json
{
  "Authorization": "Bearer eyJraWQiOiJxdWlja1N0YXJ0IiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJhdWQiOlsiYXVkMSIsImF1ZDIiLCJhdWQzIl0sInN1YiI6InF1aWNrU3RhcnRVc2VyIiwiY2l0eUVuZyI6Ik1vc2NvdyIsImNpdHkiOiLQnNC-0YHQutCy0LAiLCJyb2xlcyI6WyJhZG1pbmlzdHJhdG9yIiwibWFuYWdlciJdLCJpc3MiOiJxdWlja1N0YXJ0R3VpZGUiLCJleHAiOjQ4OTA4NzgxNjd9.Zbs45SeJKPb2gK-lsSri4PNTgCth0A1OE7IS4HRV8o4LXtc0PkmB3uAC7tgHwo2MfAH2bHvMsfPYTFoxsIK474ZVS-58-6MUDX9gMUQSAxtfnuDRmFoERk9VfvbUV44taFbZQ0FLGmZYaotjqe4PFwAUMC9BJ8w6EclJ6bb4E2o"
}
```
В присутствующем в заголовке JWT содержатся ограничения:
- получение только клиники в "г.Москва"

![jwt.png](documentation/images/readme/jwt.png)

Создайте дополнительную клинику в другом населенном пункте сменив значение Variables:

```
{
  "input": {
    "name": "Городская поликлиника №1",
    "address": {
      "city": "Торжок",
      "street": "ул. Ленина",
      "flatNo": "23"
    }
  }
}

```
и выполнив затем запрос:

```
mutation createClinicMutation($input: _CreateClinicInput!) {
  packet {
    createClinic(input: $input) {
      id
      name
      address {
        city
        street
        flatNo
      }
    }
  }
}
```

Вновь запросим список доступных нам клиник:

```
query searchClinicsQuery {
  searchClinic {
    elems {
      id
      name
      address {
        city
      }
    }
  }
}
```

Согласно функционалу Security в ответе по-прежнему будет присутствовать только клиника в г.Москва

Чтобы узнать больше о возможностях Security, ознакомьтесь с [документацией по Security](documentation/security.md)

#### Взаимодействие с GraphQL без UI:



```bash
curl -X POST \
     -H "Authorization: Bearer eyJraWQiOiJxdWlja1N0YXJ0IiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJhdWQiOlsiYXVkMSIsImF1ZDIiLCJhdWQzIl0sInN1YiI6InF1aWNrU3RhcnRVc2VyIiwiY2l0eUVuZyI6Ik1vc2NvdyIsImNpdHkiOiLQnNC-0YHQutCy0LAiLCJyb2xlcyI6WyJhZG1pbmlzdHJhdG9yIiwibWFuYWdlciJdLCJpc3MiOiJxdWlja1N0YXJ0R3VpZGUiLCJleHAiOjQ4OTA4NzgxNjd9.Zbs45SeJKPb2gK-lsSri4PNTgCth0A1OE7IS4HRV8o4LXtc0PkmB3uAC7tgHwo2MfAH2bHvMsfPYTFoxsIK474ZVS-58-6MUDX9gMUQSAxtfnuDRmFoERk9VfvbUV44taFbZQ0FLGmZYaotjqe4PFwAUMC9BJ8w6EclJ6bb4E2o" \
     -H 'Content-Type: application/json' \
     -d '{"query": "query searchClinicsQuery { searchClinic { elems { id name address { city } } } }"}' \
     http://localhost:8080/models/1/graphql
```
получим ответ:

```bash
{"data":{"searchClinic":{"elems":[{"id":"ad44313c-2308-45a4-ae4f-6596fd0d1cdb","name":"Городская поликлиника №1","address":{"city":"Москва"}}]}}}
```


Текущий проект запущен с демонстрационной моделью "Медицинская клиника: запись на прием".

Для расширения/изменения этой модели обратитесь к разделу [Изменение модели данных](./documentation/model-guide.md) в [model-guide.md](./documentation/model-guide.md)



## Системные требования DataSpace Community Edition
* Поддерживаемые платформы: x86, x64
* Поддерживаемые операционные системы: Linux, Windows, Mac OS
* Минимальная конфигурация аппаратного обеспечения: 1 CPU, 256Mb ОЗУ

Для дальнейшего ознакомления с Platform V DataSpace Community Edition обратитесь к [документации](documentation).<br>
