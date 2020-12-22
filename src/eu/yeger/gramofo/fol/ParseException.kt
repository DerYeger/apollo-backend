package eu.yeger.gramofo.fol

/**
 * This class is used to represent a exception while parsing a sequence. The
 * index is used to determine the position of the input array, where the error
 * occurred to create useful error messages.
 *
 */
class ParseException(message: String?) : Exception(message)
