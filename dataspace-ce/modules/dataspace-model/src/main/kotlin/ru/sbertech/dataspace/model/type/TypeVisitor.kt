package ru.sbertech.dataspace.model.type

interface TypeVisitor<out R> : TypeParameterizedVisitor<Unit, R>
