package ru.sbertech.dataspace.sql.table

interface TableVisitor<out R> : TableParameterizedVisitor<Unit, R>
