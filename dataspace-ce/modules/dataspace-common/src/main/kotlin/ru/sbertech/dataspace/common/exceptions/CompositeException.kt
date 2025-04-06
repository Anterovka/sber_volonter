package ru.sbertech.dataspace.common.exceptions

import java.util.StringJoiner

class CompositeException :
    AbstractDeepException,
    DeepException {
    private val exceptionList: MutableList<Throwable> = ArrayList()
    val size: Int
        get() = exceptionList.size
    val isEmpty: Boolean
        get() = exceptionList.isEmpty()
    override val message: String
        get() = getMessage(System.lineSeparator(), false)

    constructor(message: String?) : super(message)
    constructor(cause: Throwable?) : super(cause)

    fun addException(ex: Throwable) {
        exceptionList.add(ex)
    }

    override fun getDeepMessage(): String = getMessage(System.lineSeparator(), true)

    private fun getMessage(
        delimiter: String,
        deepMessage: Boolean,
    ): String {
        val sj = StringJoiner(delimiter)
        var idx = 1
        for (ex in exceptionList) {
            sj.add(
                (
                    idx++.toString() + ". " + (
                        if (deepMessage && (ex is DeepException)) {
                            (ex as DeepException).getDeepMessage()
                        } else {
                            ex.message
                        }
                    )
                ),
            )
        }
        return (
            if (deepMessage) {
                deepMessageInner()
            } else {
                super.message
            }
        ) + (
            if (sj.length() > 0) {
                delimiter + "Cause:" + delimiter + sj
            } else {
                ""
            }
        )
    }

    private fun deepMessageInner(): String? {
        if (cause == null) {
            return super.message
        }

        val sb = StringBuilder(getDeepMessage())
        var ex = this.cause
        while (ex != null) {
            sb.append(System.lineSeparator())
            sb.append("cause: ")
            sb.append(ex.message)
            ex = ex.cause
        }
        return sb.toString()
    }
}
