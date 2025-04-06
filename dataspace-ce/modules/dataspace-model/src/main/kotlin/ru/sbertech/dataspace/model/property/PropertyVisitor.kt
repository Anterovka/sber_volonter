package ru.sbertech.dataspace.model.property

interface PropertyVisitor<out R> : PropertyParameterizedVisitor<Unit, R>
