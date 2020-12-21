package eu.yeger.gramofo.fol

import java.lang.Exception

/**
 * This class is used to represent a exception while parsing a sequence. The
 * index is used to determine the position of the input array, where the error
 * occurred to create useful error messages.
 *
 */
class ParseException
/**
 * Creates a new instance of a parse error with a error message and the
 * index, where the error occurred in the input array.
 *
 * @param string
 */
(string: String?) : Exception(string) {
    companion object {
        /**
         *
         */
        private const val serialVersionUID = -246316868958775071L
    }
}
