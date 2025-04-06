package ru.sbertech.dataspace.sql.dialect.postgres

internal enum class ExprPriority {
    VALUE,
    CAST,
    NEG,
    MUL,
    ADD,
    COMPARISON,
    NOT,
    AND,
    OR,
}
