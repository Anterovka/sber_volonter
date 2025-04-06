package ru.sbertech.dataspace.sql.expr

interface ExprVisitor<out R> : ExprParameterizedVisitor<Unit, R>
