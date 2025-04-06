package ru.sbertech.dataspace.sql.subquery

interface SubQueryVisitor<out R> : SubQueryParameterizedVisitor<Unit, R>
