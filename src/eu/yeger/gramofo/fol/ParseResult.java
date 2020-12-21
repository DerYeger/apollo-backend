package eu.yeger.gramofo.fol;

/**
 * This class is used to represent the result of a parsing operation,
 * independent of the success of the operation. If it was not successful, it
 * will contain a error message with useful information, where the error occurred
 * within the input.
 */
public class ParseResult<T> {

	private T result;
	private String errorMessage;

	/**
	 * Creates a successful parse result.
	 * @param result the parsed result
	 */
	public ParseResult(T result) {
		this.result = result;
		this.errorMessage = null;
	}

	/**
	 * Creates a failed parse result.
	 * @param errorMessage a html formatted string, which describes the error
	 */
	public ParseResult(String errorMessage) {
		this.result = null;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for the result.
	 * @return if this ParseResult is successful it returns an instance of T, otherwise it returns null.
	 */
	public T getResult() {
		return result;
	}

	/**
	 * Getter for the error message.
	 * @return if this is a failed ParseResult it returns a error message, formatted as html-string. Otherwise it returns null.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @return true, if this result was not successful.
	 */
	public boolean isError() {
		return errorMessage != null;
	}
}
