package eu.yeger.gramofo.fol.parser

/**
 * This class is used to represent the result of a parsing operation,
 * independent of the success of the operation. If it was not successful, it
 * will contain a error message with useful information, where the error occurred
 * within the input.
 */
class ParseResult<T> {
    /**
     * Getter for the result.
     * @return if this ParseResult is successful it returns an instance of T, otherwise it returns null.
     */
    var result: T?
        private set

    /**
     * Getter for the error message.
     * @return if this is a failed ParseResult it returns a error message, formatted as html-string. Otherwise it returns null.
     */
    var errorMessage: String?
        private set

    /**
     * Creates a successful parse result.
     * @param result the parsed result
     */
    constructor(result: T) {
        this.result = result
        errorMessage = null
    }

    /**
     * Creates a failed parse result.
     * @param errorMessage a html formatted string, which describes the error
     */
    constructor(errorMessage: String?) {
        result = null
        this.errorMessage = errorMessage
    }

    /**
     * @return true, if this result was not successful.
     */
    val isError: Boolean
        get() = errorMessage != null
}
