package ru.sbertech.dataspace.common

class SubCharSequence(
    private val charSequence: CharSequence,
    private val startIndex: Int = 0,
    endIndex: Int = charSequence.length,
) : CharSequence {
    override val length = endIndex - startIndex

    override fun get(index: Int) = charSequence[startIndex + index]

    override fun subSequence(
        startIndex: Int,
        endIndex: Int,
    ) = SubCharSequence(charSequence, this.startIndex + startIndex, this.startIndex + endIndex)

    override fun toString() = charSequence.substring(startIndex, startIndex + length)
}
