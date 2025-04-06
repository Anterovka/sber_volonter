package ru.sbertech.dataspace.primitive.type

interface PrimitiveTypeVisitor<out R> : PrimitiveTypeParameterizedVisitor<Unit, R>
