package ru.sbertech.dataspace.common.exceptions

abstract class AbstractDeepException :
    RuntimeException,
    DeepException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?) : super(message, cause)

    override fun getDeepMessage(): String {
        val deepMessage =
            when (cause) {
                is DeepException -> (cause as DeepException).getDeepMessage()
                else -> cause?.message.orEmpty()
            }

        return if (!message.isNullOrEmpty()) {
            "$message: $deepMessage"
        } else {
            deepMessage
        }
    }
}
