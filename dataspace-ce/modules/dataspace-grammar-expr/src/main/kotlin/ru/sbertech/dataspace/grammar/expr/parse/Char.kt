package ru.sbertech.dataspace.grammar.expr.parse

internal val Char.isSpace: Boolean get() = this == ' ' || this == '\r' || this == '\n' || this == '\t'

internal val Char.isDigit: Boolean get() = this in '0'..'9'

internal val Char.isLetter: Boolean get() = this in 'a'..'z' || this in 'A'..'Z'

internal val Char.isPartOfName: Boolean get() = isLetter || this == '_' || isDigit
