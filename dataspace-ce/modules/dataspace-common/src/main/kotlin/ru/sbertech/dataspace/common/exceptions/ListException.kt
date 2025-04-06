package ru.sbertech.dataspace.common.exceptions

class ListException : AbstractDeepException() {
    private val errors: MutableList<String> = ArrayList()
    val size: Int
        get() = errors.size
    val isEmpty: Boolean
        get() = errors.isEmpty()
    override val message: String
        get() = errors.joinToString(SEPARATOR, "[$SEPARATOR", "$SEPARATOR]")

    fun addErrorMessage(errorMessage: String) {
        errors.add(errorMessage)
    }

    companion object {
        private val SEPARATOR = System.lineSeparator()
    }
}
