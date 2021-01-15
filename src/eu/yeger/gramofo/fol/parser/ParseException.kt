package eu.yeger.gramofo.fol.parser

/**
 * Custom exception for parsing formulas.
 *
 * This is legacy code.
 *
 * @property message The message of the exception. Required.
 * @constructor Creates a [ParseException] with the given [message].
 */
internal class ParseException(override val message: String) : Exception(message)
