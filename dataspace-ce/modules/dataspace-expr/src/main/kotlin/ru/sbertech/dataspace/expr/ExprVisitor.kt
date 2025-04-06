package ru.sbertech.dataspace.expr

interface ExprVisitor<out R> : ExprParameterizedVisitor<Unit, R>
