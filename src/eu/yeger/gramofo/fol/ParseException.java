package eu.yeger.gramofo.fol;

/**
 * This class is used to represent a exception while parsing a sequence. The
 * index is used to determine the position of the input array, where the error
 * occurred to create useful error messages.
 * 
 */
public class ParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -246316868958775071L;


	/**
	 * Creates a new instance of a parse error with a error message and the
	 * index, where the error occurred in the input array.
	 * 
	 * @param string
	 */
	public ParseException(String string) {
		super(string);

	}

}
