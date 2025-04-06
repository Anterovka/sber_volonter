package ru.sbertech.dataspace.common

fun CharSequence.indexOf(
    charSequence: CharSequence,
    startIndex: Int = 0,
    endIndex: Int = length,
): Int {
    for (index in startIndex..(endIndex - charSequence.length)) {
        if (regionMatches(index, charSequence, 0, charSequence.length)) return index
    }
    return -1
}

fun CharSequence.replaceTo(
    appendable: Appendable,
    oldValue: CharSequence,
    newValue: CharSequence,
    startIndex: Int = 0,
    endIndex: Int = length,
) {
    var index = startIndex
    while (true) {
        val nextIndex = indexOf(oldValue, index, endIndex)
        if (nextIndex == -1) break
        appendable.append(this, index, nextIndex).append(newValue)
        index = nextIndex + oldValue.length
    }
    appendable.append(this, index, endIndex)
}
