package ru.sbertech.dataspace.model.pdm

import com.sbt.mg.data.model.ClassStrategy
import ru.sbertech.dataspace.model.InheritanceStrategy
import java.util.EnumMap

private val inheritanceStrategyByClassStrategy: Map<ClassStrategy, InheritanceStrategy> =
    EnumMap<ClassStrategy, InheritanceStrategy>(ClassStrategy::class.java).apply {
        put(ClassStrategy.SINGLE_TABLE, InheritanceStrategy.SINGLE_TABLE)
        put(ClassStrategy.JOINED, InheritanceStrategy.JOINED)
    }

fun inheritanceStrategy(classStrategy: ClassStrategy): InheritanceStrategy = inheritanceStrategyByClassStrategy.getValue(classStrategy)
