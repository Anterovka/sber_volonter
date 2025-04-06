package ru.sbertech.dataspace.primitive

interface PrimitiveVisitor<out R> : PrimitiveParameterizedVisitor<Unit, R>
