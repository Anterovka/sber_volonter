package ru.sbertech.dataspace.sql.query

interface QueryVisitor<out R> : QueryParameterizedVisitor<Unit, R>
