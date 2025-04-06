# Обзор технологии автоматической кодогенерации в проекте "Трекер задач"

## Введение

Автоматическая кодогенерация — это технология, которая позволяет автоматически создавать код на основе схем, спецификаций или других исходных данных. В контексте GraphQL-приложений кодогенерация особенно полезна, поскольку позволяет автоматически создавать типизированные интерфейсы, запросы и мутации на основе GraphQL-схемы.

В проекте "Трекер задач" используются два типа кодогенерации:
1. **Генерация типов и запросов (codegen)** — создание TypeScript-типов и хуков React Apollo на основе GraphQL-схемы и запросов
2. **Генерация разрешений (permgen)** — создание JSON-файла с разрешениями для GraphQL-запросов

## Генерация типов и запросов (codegen)

### Обзор

Генерация типов и запросов в проекте реализована с использованием библиотеки [GraphQL Code Generator](https://www.graphql-code-generator.com/). Эта библиотека анализирует GraphQL-схему и запросы, определенные в проекте, и генерирует TypeScript-типы и хуки React Apollo для работы с этими запросами.

### Основные файлы

- **Конфигурационный файл**: `codegen.ts`
- **Входные файлы**: 
  - GraphQL-схема: `./graphql/schema.graphql`
  - GraphQL-запросы: `./graphql/**/*.graphql`
- **Выходной файл**: `./src/__generate/requests.ts`

### Процесс кодогенерации

1. **Запуск**: Кодогенерация запускается командой `npm run codegen`
2. **Чтение конфигурации**: Система читает конфигурационный файл `codegen.ts`
3. **Анализ схемы и запросов**: Система анализирует GraphQL-схему и запросы
4. **Трансформация документов**: Применяются трансформации к документам (например, обработка строковых шаблонов)
5. **Генерация кода**: Генерируется TypeScript-код с типами и хуками React Apollo
6. **Сохранение результата**: Сгенерированный код сохраняется в файл `./src/__generate/requests.ts`

### Пример входных данных

**GraphQL-схема (фрагмент из schema.graphql):**
```graphql
interface Customer {
  id: ID!
  aggVersion: Long!
  chgCnt: Long
  lastChangeDate: _DateTime
  ownerId: String
  email: String
  login: String
  type: String!
  _calc: _Calculation!
}
```

**GraphQL-запрос (фрагмент из requests.graphql):**
```graphql
fragment CustomerAttributes on _E_Customer {
  id
  login
  email
}

query getCustomerInfo($cond: String!) {
  searchCustomer(cond: $cond) {
    elems {
      ...CustomerAttributes
    }
  }
}
```

### Пример выходных данных

**Сгенерированные типы и хуки (фрагмент из requests.ts):**
```typescript
export type CustomerAttributesFragment = { __typename?: '_E_Customer', id: string, login?: string | null, email?: string | null };

export type GetCustomerInfoQueryVariables = Exact<{
  cond: Scalars['String']['input'];
}>;

export type GetCustomerInfoQuery = { __typename?: '_Query', searchCustomer: { __typename?: '_EC_Customer', elems: Array<{ __typename?: '_E_Customer', id: string, login?: string | null, email?: string | null }> } };

export function useGetCustomerInfoQuery(baseOptions: Apollo.QueryHookOptions<GetCustomerInfoQuery, GetCustomerInfoQueryVariables> & ({ variables: GetCustomerInfoQueryVariables; skip?: boolean; } | { skip: boolean; }) ) {
  // ...
}
```

### Особенности реализации

В проекте "Трекер задач" кодогенерация типов и запросов имеет следующие особенности:

1. **Использование плагинов**:
   - `typescript` — генерация базовых TypeScript-типов
   - `typescript-operations` — генерация типов для операций (запросов, мутаций)
   - `typescript-react-apollo` — генерация React-хуков для Apollo Client

2. **Трансформация документов**: В конфигурации определена трансформация документов, которая обрабатывает строковые шаблоны в GraphQL-запросах, заменяя `${` на `TOVvvVOT${` для предотвращения конфликтов при интерполяции строк.

## Генерация разрешений (permgen)

### Обзор

Генерация разрешений в проекте реализована с использованием собственного плагина `perm-plugin.js` для GraphQL Code Generator. Этот плагин анализирует GraphQL-запросы и создает JSON-файл с разрешениями для этих запросов.

### Основные файлы

- **Конфигурационный файл**: `permgen.ts`
- **Файл плагина**: `perm-plugin.js`
- **Входные файлы**: 
  - GraphQL-схема: `./graphql/schema.graphql`
  - GraphQL-запросы: `./graphql/**/*.graphql`
- **Выходной файл**: `./dumme-permissions.json`

### Процесс кодогенерации

1. **Запуск**: Кодогенерация запускается командой `npm run permgen`
2. **Чтение конфигурации**: Система читает конфигурационный файл `permgen.ts`
3. **Загрузка плагина**: Система загружает плагин `perm-plugin.js`
4. **Анализ схемы и запросов**: Плагин анализирует GraphQL-схему и запросы
5. **Генерация разрешений**: Плагин генерирует JSON-структуру с разрешениями для запросов
6. **Сохранение результата**: Сгенерированные разрешения сохраняются в файл `./dumme-permissions.json`

### Пример входных данных

**GraphQL-запрос (фрагмент из requests.graphql):**
```graphql
query getCustomerInfo($cond: String!) {
  searchCustomer(cond: $cond) {
    elems {
      ...CustomerAttributes
    }
  }
}
```

### Пример выходных данных

**Сгенерированные разрешения (фрагмент из dumme-permissions.json):**
```json
{
  "name": "getCustomerInfo",
  "body": "query getCustomerInfo($cond: String!) {\n  searchCustomer(cond: $cond) {\n    elems {\n      ...CustomerAttributes\n    }\n  }\n}\n    fragment CustomerAttributes on _E_Customer {\n  id\n  login\n  email\n}",
  "allowEmptyChecks": true,
  "disableJwtVerification": true
}
```

### Особенности реализации

В проекте "Трекер задач" генерация разрешений имеет следующие особенности:

1. **Собственный плагин**: Используется собственный плагин `perm-plugin.js`, который расширяет функциональность GraphQL Code Generator
2. **Интеграция с React Apollo**: Плагин использует `ReactApolloVisitor` из пакета `@graphql-codegen/typescript-react-apollo` для анализа GraphQL-запросов
3. **Формат разрешений**: Каждое разрешение включает имя операции, тело запроса и дополнительные флаги (`allowEmptyChecks`, `disableJwtVerification`)

## Преимущества технологии автоматической кодогенерации

1. **Типобезопасность**: Автоматически сгенерированные типы обеспечивают строгую типизацию в TypeScript, что помогает избежать ошибок во время разработки
2. **Согласованность**: Типы и запросы всегда соответствуют актуальной GraphQL-схеме
3. **Производительность разработки**: Разработчикам не нужно вручную писать типы и запросы, что экономит время
4. **Удобство использования**: Сгенерированные хуки React Apollo упрощают интеграцию GraphQL-запросов в React-компоненты
5. **Управление разрешениями**: Автоматическая генерация разрешений обеспечивает согласованность между клиентскими запросами и серверными разрешениями

## Полезные ссылки

- [GraphQL Code Generator](https://www.graphql-code-generator.com/) - официальная документация GraphQL Code Generator
- [Apollo Client](https://www.apollographql.com/docs/react/) - документация Apollo Client для React
- [TypeScript](https://www.typescriptlang.org/docs/) - документация TypeScript
- [GraphQL](https://graphql.org/learn/) - документация GraphQL
