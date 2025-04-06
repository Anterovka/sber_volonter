package ru.sbertech.dataspace.entitymanager.selector

interface SelectorVisitor<out R> : SelectorParameterizedVisitor<Unit, R>
