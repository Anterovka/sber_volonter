package ru.sbertech.dataspace.common.exceptions

class SingularDeepException(
    message: String,
    error: Throwable,
) : AbstractDeepException(message, error) {
    override fun getDeepMessage(): String =
        message + SEPARATOR +
            if (cause is DeepException) {
                (cause as DeepException).getDeepMessage()
            } else {
                cause?.message ?: ""
            }

    companion object {
        private val SEPARATOR = System.lineSeparator()
    }
}
